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
  object table extends CompositeKeyTable("tabula_test", id, name, service.region)

  test("complex example") {
    println("creating table")
    val ac = CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
    val (_, _, state) =  service.please(ac)(createCompositeKeyTableExecute)


    waitFor(table, state).foreach { st =>
      import ohnosequences.scarph._

      case object TestItem extends ItemType(table)
      implicit val TestItemType_id = TestItem has id
      implicit val TestItemType_name = TestItem has name

      case object testItem extends AnyItem {
        type Tpe = TestItem.type
        val  tpe = TestItem
        type Raw = (Int, String)

        // implicit def getId: GetProperty[id.type] = new GetProperty(id) {
        //   def apply(rep: Rep): id.Raw = rep._1
        // }
      }

      val myItem: testItem.Rep = testItem ->> ((213, "test"): (Int, String))

      implicit def getSDKRep(rep: testItem.Rep): Map[String, AttributeValue] = {
        Map[String, AttributeValue](
          id.label -> rep._1,
          name.label -> rep._2
        )
      }

      val putAc = InTable(table, st) putItem testItem ofValue myItem

      service please putAc
      // service.please(putAc)(x => putItemCompositeKeyExecutor(x)(defaultDynamoDBClient, getSDKRep))

      service please DeleteTable(table, st)
    }
    //   implicit val ac = GetItemCompositeKey(table, a, testItem, 213, "test", myItem)(testItemType_id, testItemType_name)

    //   implicit def parseSDKRep(rep:  Map[String, AttributeValue]): testItem.Rep = {
    //     testItem ->> ((rep(id.label).getN.toInt, rep(name.label).getS.toString))
    //   }
    //   implicit val p: RepFromMap.Aux[ac.type, testItem.Rep] = new RepFromMap[ac.type] {
    //     // val a = ac
    //     type Out = testItem.Rep
    //     def apply(m: Map[String, AttributeValue]): Out =
    //       testItem ->> ((m(id.label).getN.toInt, m(name.label).getS.toString))
    //   }

    //   // println((myItem: testItem.Rep).getClass)
    //   // [table.type, testItem.Rep, testItem.type]

    //   // FIXME!!!! (nothing works)
    //    // val (output, _, _) = service.apply(ac)//(getItemCompositeKeyExecutor(defaultDynamoDBClient, parseSDKRep, getAttributeValue, getAttributeValueS))

    //   // println(output)
    //   // assert(output._1 === 213)
    //   // assert(output._2 === "test")

    //  // service please DeleteItemCompositeKey(table, a, 213, "test")

    //   //service please UpdateTable(table, a, 2, 2)
    //   waitFor(table, a).foreach{ x => service.apply(DeleteTable(table, x)) }
    // }
  }


}
