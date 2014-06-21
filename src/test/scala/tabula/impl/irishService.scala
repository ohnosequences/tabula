package ohnosequences.tabula.impl

import org.scalatest.FunSuite
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._


class irishService extends FunSuite {
  import Implicits._
  import Executors._

  type Id[+X] = X
  def typed[X](x: X) = x

  val service = IrishDynamoDBService

  def waitFor[
    T <: Singleton with AnyTable.inRegion[service.Region], 
    S <: AnyTableState.For[T]
  ](table: T, initialState: S): Option[Active[T]] = {

    var active = false
    var state: AnyTableState.For[T] = initialState

    var res: Option[Active[T]] = None
    while(!active) {
      val result = service please DescribeTable(table, state)
    //  println(">" + state)
      result._3 match {
        case a: Active[T] => active = true; res = Some(a)
        case _ => {
          println(table.name + " state: " + state)
          Thread.sleep(5000)
        }
      }
    }
    res
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

  test("complex example") {
    // CREATE TABLE
    val (_, _, st0) = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))

    waitFor(table, st0).foreach { st =>
      val myItem = pairItem ->> ((213, "test"))
      import pairItemImplicits._

      // PUT ITEM
      val (putResult, _, st1) = service please (InTable(table, st) putItem pairItem ofValue myItem)
      assert(putResult === PutItemSuccess)
      println("put item: " + myItem)

      waitFor(table, st1).foreach { st =>
        // GET ITEM
        val (getResult, _, st2) = service please (FromTable(table, st) getItem pairItem withKeys (myItem._1, myItem._2))
        assert(getResult === GetItemSuccess(myItem))
        println("got item: " + getResult)

        waitFor(table, st2).foreach { st =>
          // DELETE TABLE
          service please DeleteTable(table, st)
        }
      }
    }
  }

}
