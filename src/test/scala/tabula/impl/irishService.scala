package ohnosequences.tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl._
import ohnosequences.tabula._
import ohnosequences.tabula.InitialThroughput
import ohnosequences.tabula.Active
import ohnosequences.tabula.InitialThroughput
import com.amazonaws.services.dynamodbv2.model.AttributeValue

class irishService extends FunSuite {
  import Implicits._
  import Executors._

  type Id[+X] = X
  def typed[X](x: X) = x

  val service = IrishDynamoDBService

  def waitFor[T <: AnyTable with Singleton with AnyDynamoDBResource.inRegion[service.Region], S <: AnyTableState.For[T]](table: T, initialState: S): Option[Active[T]] = {
    var active = false
    var state: AnyTableState.For[T] = initialState

    var res: Option[Active[T]] = None
    while(!active) {
      state = (service please DescribeTable(table, state))._2
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
    val (_, sta): (table.type, AnyTableState.For[table.type]) = 
      service(CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1))))



    waitFor(table, sta).foreach { a =>

      case object TestItemType extends ItemType(table)
      implicit val TestItemType_id = TestItemType has id
      implicit val TestItemType_name = TestItemType has name

      import ohnosequences.scarph._

      case class ItemRep(id: Int, name: String)

      case object TestItem extends AnyItem {
        type Tpe = TestItemType.type
        val  tpe = TestItemType

        type Raw = ItemRep

//        implicit def getId: GetProperty[id.type] = new GetProperty(id) {
//          def apply(rep: Rep): id.Raw = rep._1
//        }

      }

      implicit def getSDKRep(rep: ItemRep): Map[String, AttributeValue] = {
        Map[String, AttributeValue](
          id.label -> rep.id,
          name.label -> rep.name
        )
      }


//      def getTestItemId(rep: AnyDenotation.TaggedWith[TestItem.type]): id.Raw = {
//        rep get id
//      }

      val myItem = TestItem ->> (ItemRep(1, "yeah"))
//      val myId = getTestItemId(myItem)
//      assert(myId === 3)

      service.please(PutItemCompositeKey(table, a, TestItem, myItem)(TestItemType_id, TestItemType_name))
      service please DeleteItemCompositeKey(table, a, 1, "yeah")
      //service please UpdateTable(table, a, 2, 2)
      waitFor(table, a).foreach(service please DeleteTable(table, _))
    }
  }


}
