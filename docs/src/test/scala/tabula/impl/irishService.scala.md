
```scala
package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue

import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._, AttributeImplicits._, DynamoDBExecutors._


object TestImplicits {

  implicit val defaultDynamoDBClient = new DynamoDBClient(EU,
    new AmazonDynamoDBClient(CredentialProviderChains.default)) {
    client.setRegion(Region.getRegion(Regions.EU_WEST_1))
  }

}

object TestSetting {
  case object service extends AnyDynamoDBService {
    type Region = EU.type
    val region = EU

    type Account = ohnosequences.tabula.Account
    val account: Account = Account("", "")

    def endpoint: String = "" //shouldn't be here
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
}

class irishService extends FunSuite {
  import TestImplicits._
  import TestSetting._
  import pairItemImplicits._

  type Id[+X] = X
  def typed[X](x: X) = x

  // waits until the table becomes active
  def waitFor[
    T <: Singleton with AnyTable.inRegion[service.Region], 
    S <: AnyTableState.For[T]
  ](table: T, state: S): Active[T] = {

    val result = service please DescribeTable(table, state)

    result.state match {
      case (a @ Active(table, _, _)) => return a
      case s => {
        println(table.name + " state: " + s)
        Thread.sleep(5000)
        waitFor(table, s)
      }
    }
  }

  ignore("complex example") {
    // CREATE TABLE
    val createResult = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
    val afterCreate = waitFor(table, createResult.state)

    // UPDATE TABLE (takes time)
    // val updateResult  = service please UpdateTable(table, afterCreate).withReadWriteThroughput(2, 2)
    // val afterUpdate = waitFor(table, updateResult.state)
    // val updateResult2 = service please UpdateTable(table, afterUpdate).withReadWriteThroughput(1, 1)
    // val afterUpdate2 = waitFor(table, updateResult2.state)

    // PUT ITEM
    val myItem = pairItem ->> ((213, "test"))

    val putResult = service please (InTable(table, afterCreate) putItem pairItem withValue myItem)
    assert(putResult.output === PutItemSuccess)
    val afterPut = waitFor(table, putResult.state)

    // GET ITEM
    val getResult = service please (FromCompositeKeyTable(table, afterPut) getItem pairItem withKeys (myItem._1, myItem._2))
    assert(getResult.output === GetItemSuccess(myItem))
    val afterGet = waitFor(table, getResult.state)

    // DELETE ITEM + get again
    val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterGet) withKeys (myItem._1, myItem._2))
    val afterDel = waitFor(table, delResult.state)

    // prints exception stacktrace - it's ok
    val getResult2 = service please (FromCompositeKeyTable(table, afterDel) getItem pairItem withKeys (myItem._1, myItem._2))
    assert(getResult2.output === GetItemFailure())

    // DELETE TABLE
    val lastState = waitFor(table, getResult2.state)
    service please DeleteTable(table, lastState)
  }

}

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + impl
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
          + [AttributeImplicits.scala][main/scala/tabula/impl/AttributeImplicits.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]

[test/scala/tabula/simpleModel.scala]: ../simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: irishService.scala.md
[main/scala/tabula.scala]: ../../../../main/scala/tabula.scala.md
[main/scala/tabula/predicates.scala]: ../../../../main/scala/tabula/predicates.scala.md
[main/scala/tabula/accounts.scala]: ../../../../main/scala/tabula/accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../../../../main/scala/tabula/impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../../../../main/scala/tabula/impl/Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../../../../main/scala/tabula/impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../../../../main/scala/tabula/impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../../../../main/scala/tabula/impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../../../../main/scala/tabula/impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../../../../main/scala/tabula/impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../../../../main/scala/tabula/impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../../../../main/scala/tabula/impl/executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: ../../../../main/scala/tabula/impl/AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: ../../../../main/scala/tabula/regions.scala.md
[main/scala/tabula/states.scala]: ../../../../main/scala/tabula/states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../../../../main/scala/tabula/actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../../../../main/scala/tabula/actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../../../../main/scala/tabula/actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../../../../main/scala/tabula/actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../../../../main/scala/tabula/actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../../../../main/scala/tabula/actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../../../../main/scala/tabula/actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: ../../../../main/scala/tabula/executors.scala.md
[main/scala/tabula/items.scala]: ../../../../main/scala/tabula/items.scala.md
[main/scala/tabula/resources.scala]: ../../../../main/scala/tabula/resources.scala.md
[main/scala/tabula/actions.scala]: ../../../../main/scala/tabula/actions.scala.md
[main/scala/tabula/tables.scala]: ../../../../main/scala/tabula/tables.scala.md
[main/scala/tabula/attributes.scala]: ../../../../main/scala/tabula/attributes.scala.md
[main/scala/tabula/services.scala]: ../../../../main/scala/tabula/services.scala.md
[main/scala/tabula/conditions.scala]: ../../../../main/scala/tabula/conditions.scala.md