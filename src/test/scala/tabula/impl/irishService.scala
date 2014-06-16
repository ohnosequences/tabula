package tabula.impl

import org.scalatest.FunSuite
import ohnosequences.tabula.impl.{ReadyToDelete, DeleteTable, CredentialProviderChains, IrishDynamoDBService}
import ohnosequences.tabula.{InitialThroughput, InitialState, Attribute, HashKeyTable}

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

    import service._

    service.apply( new DeleteTable(table, InitialState(table, service.account, InitialThroughput(0, 0))))
  }
}
