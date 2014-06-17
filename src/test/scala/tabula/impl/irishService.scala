package ohnosequences.tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl._
import ohnosequences.tabula._
import ohnosequences.tabula.InitialThroughput
import ohnosequences.tabula.Active
import ohnosequences.tabula.InitialThroughput

class irishService extends FunSuite {
  import Implicits._

  type Id[+X] = X

  test("test credentials") {
    val service = new IrishDynamoDBService(CredentialProviderChains.default)

    assert(!service.ddbClient.listTables().getTableNames.isEmpty)
  }

  test("deleting table") {

    //wordcount01_snapshot_errors
    case object id extends Attribute[Int]

    val service = new IrishDynamoDBService(CredentialProviderChains.default)
    object table extends HashKeyTable("wordcount01_snapshot_errors", id, service.region)



    service.apply[DeleteTable[table.type], Id]( new DeleteTable(table, Active(table, service.account, InitialThroughput(0, 0))))
  }

  test("creating table") {
    case object id extends Attribute[Int]



    val service = new IrishDynamoDBService(CredentialProviderChains.default)
    object table extends HashKeyTable("tabula_test2", id, service.region)

    service.apply[CreateHashKeyTable[id.type, service.Region, table.type], Id](new CreateHashKeyTable[id.type, service.Region, table.type](table, InitialState(table, service.account, InitialThroughput(1, 1))))
  }

  test("complex example") {
    case object id extends Attribute[Int]

    val service = new IrishDynamoDBService(CredentialProviderChains.default)

    object table extends HashKeyTable("tabula_test", id, service.region)

    println("creating table tabula_test")
    val (in, sta): (table.type, AnyTableState.For[table.type]) = service.apply[CreateHashKeyTable[id.type, service.Region, table.type], Id](new CreateHashKeyTable[id.type, service.Region, table.type](table, InitialState(table, service.account, InitialThroughput(1, 1))))


    println("tabula_test status: " + sta)

    var status = sta
    var deleted = false
    while (!deleted) {
      status = (service.apply[GetTable[table.type], Id](new GetTable(table, status)))._2
      println("tabula_test status: " + status)

      status match {
        case active: Active[table.type] => {
          println("deleting tabula_test")
          service.apply[DeleteTable[table.type], Id]( new DeleteTable(table, status))
        }
      }
      Thread.sleep(5000)
    }
  }


}
