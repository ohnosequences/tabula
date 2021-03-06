
```scala
package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue} //, PropertyAction}

import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._, actions._, ImplicitConversions._

import shapeless._, poly._
import shapeless.test.typed
import AnyTag._

object TestSetting {
  
    case object service extends AnyDynamoDBService {

      type Region = EU.type
      val  region = EU

      type Account = ohnosequences.tabula.Account
      val  account: Account = Account("", "")

      def endpoint: String = "" //shouldn't be here
    }

  val executors = DynamoDBExecutors(
    new DynamoDBClient(EU,
      new AmazonDynamoDBClient(CredentialProviderChains.default)) {
        client.setRegion(Region.getRegion(Regions.EU_WEST_1))
      }
    )

  case object id extends Property[Num]
  case object name extends Property[String]
  case object simpleUserRecord extends Record(id :~: name :~: ?)
  case object normalUserRecord extends Record(id :~: name :~: email :~: color :~: ?)

  case object table extends CompositeKeyTable("tabula_test_1", id, name, service.region)

  case object simpleUser extends Item(table, simpleUserRecord)


  // more properties:
  case object email extends Property[String]
  case object color extends Property[String]

  case object normalUser extends Item(table, normalUserRecord)
}

class irishService extends FunSuite {
  import TestSetting._
  import executors._

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

  test("complex example") {
    import toSDKRep._
    import fromSDKRep._
    import Condition._
    import AnyPredicate._

    // CREATE TABLE
    val createResult = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
    val afterCreate = waitFor(table, createResult.state)

    // UPDATE TABLE (takes time)
    // val updateResult  = service please UpdateTable(table, afterCreate).withReadWriteThroughput(2, 2)
    // val afterUpdate = waitFor(table, updateResult.state)
    // val updateResult2 = service please UpdateTable(table, afterUpdate).withReadWriteThroughput(1, 1)
    // val afterUpdate2 = waitFor(table, updateResult2.state)

    // PUT ITEM
    val user1 = normalUser fields (
      (id ->> 1) :~: 
      (name ->> "Edu") :~: 
      (email ->> "eparejatobes@ohnosequences.com") :~:
      (color ->> "verde") :~:
      ?
    )

    val user2 = normalUser fields (
      (id ->> 1) :~: 
      (name ->> "Evdokim") :~: 
      (email ->> "evdokim@ohnosequences.com") :~:
      (color ->> "negro") :~:
      ?
    )

    val user3 = normalUser fields (
      (id ->> 3) :~: 
      (name ->> "Lyosha") :~: 
      (email ->> "aalekhin@ohnosequences.com") :~:
      (color ->> "albero") :~:
      ?
    )

    val putResul1 = service please (InCompositeKeyTable(table, afterCreate) putItem normalUser withValue user1)
    assert(putResul1.output === PutItemSuccess)
    val afterPut1 = waitFor(table, putResul1.state)

    val putResul2 = service please (InCompositeKeyTable(table, afterPut1) putItem normalUser withValue user2)
    assert(putResul2.output === PutItemSuccess)
    val afterPut2 = waitFor(table, putResul2.state)

    val putResult3 = service please (

      InCompositeKeyTable(table, afterPut2) putItem simpleUser withValue (

        simpleUser fields ((user3 as simpleUser.record):simpleUser.record.Raw)
      )
    )
    assert(putResult3.output === PutItemSuccess)
    val afterPut3 = waitFor(table, putResult3.state)

    // QUERY TABLE

    // here we get both users by the hash key
    val simpleQueryResult = service please (QueryTable(table, afterPut3) forItem normalUser 
                                            withHashKey user1.get(id))
    assert(simpleQueryResult.output === QuerySuccess(List(user1, user2)))

    // here we would get the same, but we add a range condition on the name
    val normalQueryResult = service please (QueryTable(table, afterPut3) forItem normalUser
                                            withHashKey user1.get(id) 
                                            andRangeCondition (name beginsWith "Evd"))
    assert(normalQueryResult.output === QuerySuccess(List(user2)))

    // here we don't get anything
    val emptyQueryResult = service please (QueryTable(table, afterPut3) forItem normalUser 
                                            withHashKey user1.get(id) 
                                            andRangeCondition (name beginsWith "foo"))
    assert(emptyQueryResult.output === QuerySuccess(List()))

    // TODO: change syntax to something nicer. maybe smth like this:
    // (users, afterPut3) query normalUser hash 123 range (name beginsWith "my")

    // GET ITEM
    // NOTE: here we check that we can get a simpleUser instead of the normalUser and we will get only those properties
    val getResult = service please (FromCompositeKeyTable(table, afterPut3) getItem simpleUser withKeys (user1.get(id), user1.get(name)))
    assert(getResult.output === GetItemSuccess(
      simpleUser fields ((id is 1) :~: (name is "Edu") :~: ?)
    ))

    // DELETE ITEM + get again
    val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterPut3) withKeys (user1.get(id), user1.get(name)))
    val afterDel = waitFor(table, delResult.state)
    val getResult2 = service please (FromCompositeKeyTable(table, afterDel) getItem normalUser withKeys (user1.get(id), user1.get(name)))
    assert(getResult2.output === GetItemFailure("java.lang.NullPointerException"))

    // DELETE TABLE
    val lastState = waitFor(table, getResult2.state)
    service please DeleteTable(table, lastState)

  }

}

```


------

### Index

+ src
  + main
    + scala
      + tabula
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
          + [Query.scala][main/scala/tabula/actions/Query.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + impl
          + actions
            + [GetItem.scala][main/scala/tabula/impl/actions/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/actions/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/actions/Query.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/executors/Query.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
          + [ImplicitConversions.scala][main/scala/tabula/impl/ImplicitConversions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
      + [tabula.scala][main/scala/tabula.scala]
  + test
    + scala
      + tabula
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
        + [items.scala][test/scala/tabula/items.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]

[main/scala/tabula/accounts.scala]: ../../../../main/scala/tabula/accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../../../../main/scala/tabula/actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../../../../main/scala/tabula/actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../../../../main/scala/tabula/actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../../../../main/scala/tabula/actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../../../../main/scala/tabula/actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../../../../main/scala/tabula/actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: ../../../../main/scala/tabula/actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../../../../main/scala/tabula/actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: ../../../../main/scala/tabula/actions.scala.md
[main/scala/tabula/conditions.scala]: ../../../../main/scala/tabula/conditions.scala.md
[main/scala/tabula/executors.scala]: ../../../../main/scala/tabula/executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: ../../../../main/scala/tabula/impl/actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: ../../../../main/scala/tabula/impl/actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: ../../../../main/scala/tabula/impl/actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../../../../main/scala/tabula/impl/Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../../../../main/scala/tabula/impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../../../../main/scala/tabula/impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../../../../main/scala/tabula/impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../../../../main/scala/tabula/impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../../../../main/scala/tabula/impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../../../../main/scala/tabula/impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../../../../main/scala/tabula/impl/executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: ../../../../main/scala/tabula/impl/executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../../../../main/scala/tabula/impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: ../../../../main/scala/tabula/impl/ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: ../../../../main/scala/tabula/items.scala.md
[main/scala/tabula/predicates.scala]: ../../../../main/scala/tabula/predicates.scala.md
[main/scala/tabula/regions.scala]: ../../../../main/scala/tabula/regions.scala.md
[main/scala/tabula/resources.scala]: ../../../../main/scala/tabula/resources.scala.md
[main/scala/tabula/services.scala]: ../../../../main/scala/tabula/services.scala.md
[main/scala/tabula/states.scala]: ../../../../main/scala/tabula/states.scala.md
[main/scala/tabula/tables.scala]: ../../../../main/scala/tabula/tables.scala.md
[main/scala/tabula.scala]: ../../../../main/scala/tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: irishService.scala.md
[test/scala/tabula/items.scala]: ../items.scala.md
[test/scala/tabula/resourceLists.scala]: ../resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../simpleModel.scala.md