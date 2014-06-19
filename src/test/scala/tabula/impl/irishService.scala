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
      state = (service please DescribeTable(table, state))._3
    //  println(">" + state)
      state match {
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

//  test("deleting table") {
//
//    //wordcount01_snapshot_errors
//    case object id extends Attribute[Int]
//    object table extends HashKeyTable("wordcount01_snapshot_errors", id, service.region)
//
//    typed[Deleting[table.type]](service(DeleteTable(table, Active(table, service.account, InitialThroughput(1, 1))))._2)
//  }
//
//  test("creating table") {
//    case object id extends Attribute[Int]
//    object table extends HashKeyTable("tabula_test2", id, service.region)
//
//    val (in, state) = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
//
//    //don't work!
//    //service please UpdateTable(table, state, 2, 2)
//  }

  test("complex example") {
    case object id extends Attribute[Int]
    case object name extends Attribute[String]
    object table extends CompositeKeyTable("tabula_test", id, name, service.region)



    println("creating table")
    val (_, _, sta) =  service(CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1))))



    waitFor(table, sta).foreach { a =>

      case object TestItemType extends ItemType(table)
      implicit val TestItemType_id = TestItemType has id
      implicit val TestItemType_name = TestItemType has name

      import ohnosequences.scarph._

      case object TestItem extends AnyItem {
        type Tpe = TestItemType.type
        val  tpe = TestItemType

        type Raw = (Int, String)

        implicit def getId: GetProperty[id.type] = new GetProperty(id) {
          def apply(rep: Rep): id.Raw = rep._1
        }
      }

      val myItem: TestItem.Rep = TestItem ->> ((3, "foo"): (Int, String))
      // val myId = getTestItemId(myItem)
      // assert(myId === 3)

      implicit def getSDKRep(rep: TestItem.Rep): Map[String, AttributeValue] = {
        Map[String, AttributeValue](
          id.label -> rep._1,
          name.label -> rep._2
        )
      }

      implicit def parseSDKRep(rep:  Map[String, AttributeValue]): TestItem.Rep = {
        TestItem ->> (rep(id.label).getN.toInt, rep(name.label).getS.toString)
      }

      // println((myItem: TestItem.Rep).getClass)
      // [table.type, TestItem.Rep, TestItem.type]

      service please  PutItemCompositeKey(table, a, TestItem, myItem)

      val (output, _, _) = service.please(GetItemCompositeKey(table, a, TestItem, 213, "test"))(
        getItemCompositeKeyExecutor(defaultDynamoDBClient, parseSDKRep, getAttributeValue, getAttributeValueS))

      assert(output._1 === 213)
      assert(output._2 === "test")

      service please DeleteItemCompositeKey(table, a, 213, "test")

      //service please UpdateTable(table, a, 2, 2)
      waitFor(table, a).foreach(service please DeleteTable(table, _))
    }
  }


}
