package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue, AttributeAction}

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
    val region = EU

    type Account = ohnosequences.tabula.Account
    val account: Account = Account("", "")

    def endpoint: String = "" //shouldn't be here
  }

  val executors = DynamoDBExecutors(
    new DynamoDBClient(EU,
      new AmazonDynamoDBClient(CredentialProviderChains.default)) {
        client.setRegion(Region.getRegion(Regions.EU_WEST_1))
      }
    )

  case object id extends Attribute[Int]
  case object name extends Attribute[String]

  object table extends CompositeKeyTable("tabula_test_1", id, name, service.region)

  case object simpleUser extends Item(table, id :~: name :~: ∅)


  // more attributes:
  case object email extends Attribute[String]
  case object color extends Attribute[String]

  case object normalUser extends Item(table, id :~: name :~: email :~: color :~: ∅)
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

  ignore("complex example") {
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
    val user1 = normalUser ->> (
      (id ->> 1) :~: 
      (name ->> "Edu") :~: 
      (email ->> "eparejatobes@ohnosequences.com") :~:
      (color ->> "verde") :~:
      ∅
    )

    val user2 = normalUser ->> (
      (id ->> 1) :~: 
      (name ->> "Evdokim") :~: 
      (email ->> "evdokim@ohnosequences.com") :~:
      (color ->> "negro") :~:
      ∅
    )

    val user3 = normalUser ->> (
      (id ->> 3) :~: 
      (name ->> "Lyosha") :~: 
      (email ->> "aalekhin@ohnosequences.com") :~:
      (color ->> "albero") :~:
      ∅
    )

    val putResul1 = service please (InTable(table, afterCreate) putItem simpleUser withValue (user1 as simpleUser))
    assert(putResul1.output === PutItemSuccess)
    val afterPut1 = waitFor(table, putResul1.state)

    val putResul2 = service please (InTable(table, afterPut1) putItem normalUser withValue user2)
    assert(putResul2.output === PutItemSuccess)
    val afterPut2 = waitFor(table, putResul2.state)

    val putResult3 = service please (InTable(table, afterPut2) putItem normalUser withValue user3)
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
    // NOTE: here we check that we can get a simpleUser instead of the normalUser and we will get only those attributes
    val getResult = service please (FromCompositeKeyTable(table, afterPut3) getItem simpleUser withKeys (user1.get(id), user1.get(name)))
    assert(getResult.output === GetItemSuccess(
      simpleUser ->> ((id ->> 1) :~: (name ->> "Edu") :~: ∅)
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
