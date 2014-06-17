package tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl.{Implicits, DeleteTable, CredentialProviderChains, IrishDynamoDBService}
import ohnosequences.tabula._
import ohnosequences.tabula.InitialThroughput

class irishService extends FunSuite {
  test("test credentials") {
    val service = new IrishDynamoDBService(CredentialProviderChains.default)

    assert(!service.ddbClient.listTables().getTableNames.isEmpty)
  }

  test("deleting table") {

    //wordcount01_snapshot_errors
    case object id extends Attribute[Int]

    val service = new IrishDynamoDBService(CredentialProviderChains.default)
    object table extends HashKeyTable("wordcount01_snapshot_errors", id, service.region)

    import Implicits._

    service.apply( new DeleteTable(table, Active(table, service.account, InitialThroughput(0, 0))))
  }
}
