package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue, AttributeAction}

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

  test("complex example") {
    // CREATE TABLE
    val createResult = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
    val afterCreate = waitFor(table, createResult.state)

    // UPDATE TABLE (takes time)
    // val updateResult  = service please UpdateTable(table, afterCreate).withReadWriteThroughput(2, 2)
    // val afterUpdate = waitFor(table, updateResult.state)
    // val updateResult2 = service please UpdateTable(table, afterUpdate).withReadWriteThroughput(1, 1)
    // val afterUpdate2 = waitFor(table, updateResult2.state)

    // PUT ITEM
    val myItem = pairItem ->> ((213, "test"))

    val putResult = service please (InTable(table, afterCreate) putItem pairItem withValue myItem)
    assert(putResult.output === PutItemSuccess)
    val afterPut = waitFor(table, putResult.state)

    // GET ITEM
    val getResult = service please (FromCompositeKeyTable(table, afterPut) getItem pairItem withKeys (myItem._1, myItem._2))
    assert(getResult.output === GetItemSuccess(myItem))


    //negative test
   // val updateResult = service please (FromCompositeKeyTable(table, afterPut) updateItem pairItem withKeys (
   //   myItem._1, myItem._2, Map(id.label -> new AttributeValueUpdate(1, AttributeAction.ADD))))



    // DELETE ITEM + get again
    val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterPut) withKeys (myItem._1, myItem._2))
    val afterDel = waitFor(table, delResult.state)

    // prints exception stacktrace - it's ok
    val getResult2 = service please (FromCompositeKeyTable(table, afterDel) getItem pairItem withKeys (myItem._1, myItem._2))
    assert(getResult2.output === GetItemFailure())

    // DELETE TABLE
    val lastState = waitFor(table, getResult2.state)
    service please DeleteTable(table, lastState)

    //itemTest

    val myid: Int = ohnosequences.tabula.impl.itemtest.TestItem.get(ohnosequences.tabula.impl.itemtest.id, (123, "123"))
    println(myid)
    assert(myid === 123)
  }

}
