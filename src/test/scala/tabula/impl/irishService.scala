package ohnosequences.tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl._
import ohnosequences.tabula._
import ohnosequences.tabula.InitialThroughput
import ohnosequences.tabula.Active
import ohnosequences.tabula.InitialThroughput

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
    object table extends HashKeyTable("tabula_test", id, service.region)

    println("creating table tabula_test")
    val (_, sta): (table.type, AnyTableState.For[table.type]) = 
      service(CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1))))



    waitFor(table, sta).foreach { a =>

      service please DeleteItemHashKey[table.type](table, a, 213)
      service please UpdateTable(table, a, 2, 2)
      waitFor(table, a).foreach(service please DeleteTable(table, _))
    }
  }


}
