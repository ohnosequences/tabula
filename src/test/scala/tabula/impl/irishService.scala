package ohnosequences.tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl._
import ohnosequences.tabula._
import ohnosequences.tabula.InitialThroughput
import ohnosequences.tabula.Active
import ohnosequences.tabula.InitialThroughput
import ohnosequences.tabula.impl.CreateHashKeyTable.GetTable

class irishService extends FunSuite {
  import Implicits._

  test("test credentials") {
    val service = new IrishDynamoDBService(CredentialProviderChains.default)

    assert(!service.ddbClient.listTables().getTableNames.isEmpty)
  }

  test("deleting table") {

    //wordcount01_snapshot_errors
    case object id extends Attribute[Int]

    val service = new IrishDynamoDBService(CredentialProviderChains.default)
    object table extends HashKeyTable("wordcount01_snapshot_errors", id, service.region)



    service.apply( new DeleteTable(table, Active(table, service.account, InitialThroughput(0, 0))))
  }

  test("creating table") {
    case object id extends Attribute[Int]



    val service = new IrishDynamoDBService(CredentialProviderChains.default)
    object table extends HashKeyTable("tabula_test", id, service.region)

    service apply new CreateHashKeyTable[id.type, service.Region, table.type](table, InitialState(table, service.account, InitialThroughput(1, 1)))
  }

  test("complex example") {
    case object id extends Attribute[Int]

    val service = new IrishDynamoDBService(CredentialProviderChains.default)

    object table extends HashKeyTable("tabula_test", id, service.region)

    println("creating table tabula_test")
    val (in, sta) = service apply new CreateHashKeyTable[id.type, service.Region, table.type](table, InitialState(table, service.account, InitialThroughput(1, 1)))

    println("tabula_test status: " + sta)

    var status = sta
    var deleted = false
    while (!deleted) {
      status = (service apply new GetTable(table, status))._2
      println("tabula_test status: " + status)

      status match {
        case active: Active[table.type] => {
          println("deleting tabula_test")
          service apply new DeleteTable(table, active)
        }
      }
      Thread.sleep(5000)
    }
  }


}
