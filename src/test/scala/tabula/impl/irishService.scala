package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue

import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._, AttributeImplicits._, DynamoDBExecutors._


object TestImplicits {

  implicit val defaultDynamoDBClient = new DynamoDBClient(EU,
    new AmazonDynamoDBClient(CredentialProviderChains.default)) {
    client.setRegion(Region.getRegion(Regions.EU_WEST_1))
  }

}

object TestSetting {
  case object service extends AnyDynamoDBService {
    type Region = EU.type
    val region = EU

    type Account = ohnosequences.tabula.Account
    val account: Account = Account("", "")

    def endpoint: String = "" //shouldn't be here
  }

  case object id extends Attribute[Int]
  case object name extends Attribute[String]

  object table extends CompositeKeyTable("tabula_test_1", id, name, service.region)

  case object pairItem extends Item(table) { type Raw = (Int, String) }
  implicit val pairItem_props = pairItem has id :~: name :~: ∅

  object pairItemImplicits {
    implicit def getSDKRep(rep: pairItem.Rep): Map[String, AttributeValue] = {
      Map[String, AttributeValue](
        id.label -> rep._1,
        name.label -> rep._2
      )
    }

    implicit def parseSDKRep(m: Map[String, AttributeValue]): pairItem.Rep = {
      pairItem ->> ((m(id.label).getN.toInt, m(name.label).getS.toString))
    }
  }
}

class irishService extends FunSuite {
  import TestImplicits._
  import TestSetting._
  import pairItemImplicits._

  type Id[+X] = X
  def typed[X](x: X) = x

  // waits until the table becomes active
  def waitFor[
    T <: Singleton with AnyTable.inRegion[service.Region], 
    S <: AnyTableState.For[T]
  ](table: T, state: S): Active[T] = {

    val result = service please DescribeTable(table, state)

    result.state match {
      case (a @ Active(table, _, _)) => return a
      case s => {
        println(table.name + " state: " + s)
        Thread.sleep(5000)
        waitFor(table, s)
      }
    }
  }

  ignore("complex example") {
    // CREATE TABLE
    val createResult = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))

    // PUT ITEM
    val myItem = pairItem ->> ((213, "test"))

    val afterCreate = waitFor(table, createResult.state)
    val putResult = service please (InTable(table, afterCreate) putItem pairItem withValue myItem)
    assert(putResult.output === PutItemSuccess)

    // GET ITEM
    val afterPut = waitFor(table, putResult.state)
    val getResult = service please (FromTable(table, afterPut) getItem pairItem withKeys (myItem._1, myItem._2))
    assert(getResult.output === GetItemSuccess(myItem))

    // DELETE TABLE
    val afterGet = waitFor(table, getResult.state)
    service please DeleteTable(table, afterGet)
  }

}
