package ohnosequences.tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.InitialState
import ohnosequences.tabula.DeleteItemCompositeKey
import scala.Some
import ohnosequences.tabula.Active
import ohnosequences.tabula.DescribeTable
import ohnosequences.tabula.InitialThroughput
import ohnosequences.tabula.CreateTable
import ohnosequences.tabula.DeleteTable

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

  // test("test credentials") {
  //   assert(!service.ddbClient.listTables().getTableNames.isEmpty)
  // }

 ignore("deleting table") {
   //wordcount01_snapshot_errors
   case object id extends Attribute[Int]
   object table extends HashKeyTable("wordcount01_snapshot_errors", id, service.region)

   val ac = DeleteTable(table, Active(table, service.account, InitialThroughput(1, 1)))
   val res = service(ac) //(deleteTableExecute)
   typed[Deleting[table.type]](res._3)
 }

//  test("creating table") {
//    case object id extends Attribute[Int]
//    object table extends HashKeyTable("tabula_test2", id, service.region)
//
//    val (in, state) = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
//
//    //don't work!
//    //service please UpdateTable(table, state, 2, 2)
//  }

  case object id extends Attribute[Int]
  case object name extends Attribute[String]
  object table extends CompositeKeyTable("tabula_test_1", id, name, service.region)

  import ohnosequences.scarph._

  case object TestItemType extends ItemType(table)
  implicit val TestItemType_id = TestItemType has id
  implicit val TestItemType_name = TestItemType has name

  case object testItem extends AnyItem {
    type Tpe = TestItemType.type
    val  tpe = TestItemType
    type Raw = (Int, String)
  }

  val myItem: testItem.Rep = testItem ->> ((213, "test"): (Int, String))

  implicit def getSDKRep(rep: testItem.Rep): Map[String, AttributeValue] = {
    Map[String, AttributeValue](
      id.label -> rep._1,
      name.label -> rep._2
    )
  }
  implicit def parseSDKRep(m: Map[String, AttributeValue]): testItem.Rep = {
    testItem ->> ((m(id.label).getN.toInt, m(name.label).getS.toString))
  }

  test("complex example") {
    // CREATE TABLE
    val (_, _, st0) = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))

    waitFor(table, st0).foreach { st =>
      // PUT ITEM
      val (putResult, _, st1) = service please (InTable(table, st) putItem testItem ofValue myItem)
      assert(putResult === PutItemSuccess)

      waitFor(table, st1).foreach { st =>
        // GET ITEM
        val (getResult, _, st2) = service please (FromTable(table, st) getItem testItem withKeys (myItem._1, myItem._2))
        assert(getResult === GetItemSuccess(myItem))

        waitFor(table, st2).foreach { st =>
          // DELETE TABLE
          service please DeleteTable(table, st)
        }
      }
    }
  }

}
