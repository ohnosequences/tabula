package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue} //, PropertyAction}

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._

import ohnosequences.tabula._
import ohnosequences.tabula.impl._, ImplicitConversions._ //, actions._

import shapeless._, poly._
import shapeless.test.typed
import AnyTaggedType._

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
  case object simpleUserRecord extends Record(id :~: name :~: ∅)
  case object normalUserRecord extends Record(id :~: name :~: email :~: color :~: ∅)

  case object table extends Table("tabula_test_1", CompositeKey(id, name), service.region)

  case object simpleUser extends Item("simpleUser", table, simpleUserRecord.properties)


  // more properties:
  case object email extends Property[String]
  case object color extends Property[String]

  case object normalUser extends Item("normalUser", table, normalUserRecord.properties)
}

class irishService extends FunSuite {
  import TestSetting._
  import executors._

  // waits until the table becomes active
  def waitFor[
    T <: AnyTable.inRegion[service.Region], 
    S <: AnyTableState.For[T]
  ](table: T, state: S): Active[T] = {

    val exec = service please DescribeTable(table, state)
    val result = exec(state)

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
    import Condition._
    import AnyPredicate._
    import SDKRepParsers._
    import SDKRepSerializers._

    // CREATE TABLE
    val createResult = (service please CreateTable(table)).apply(InitialState(table, service.account, InitialThroughput(1, 1)))
    val afterCreate = waitFor(table, createResult.state)

    // UPDATE TABLE (takes time)
    // val updateResult  = service please UpdateTable(table, afterCreate).withReadWriteThroughput(2, 2)
    // val afterUpdate = waitFor(table, updateResult.state)
    // val updateResult2 = service please UpdateTable(table, afterUpdate).withReadWriteThroughput(1, 1)
    // val afterUpdate2 = waitFor(table, updateResult2.state)

    // PUT ITEM
    val user1 = normalUser fields (
      (id is 1) :~: 
      (name is "Edu") :~: 
      (email is "eparejatobes@ohnosequences.com") :~:
      (color is "verde") :~:
      ∅
    )

    val user2 = normalUser fields (
      (id is 1) :~: 
      (name is "Evdokim") :~: 
      (email is "evdokim@ohnosequences.com") :~:
      (color is "negro") :~:
      ∅
    )

    val user3 = normalUser fields (
      (id is 3) :~: 
      (name is "Lyosha") :~: 
      (email is "aalekhin@ohnosequences.com") :~:
      (color is "albero") :~:
      ∅
    )

// <<<<<<< HEAD
//     val foo = (user1: normalUser.Raw).serializeTo[SDKRep]

//     val putResul1 = service please putputput(afterCreate, user1)

//     // val putResul1 = service please ((InCompositeKeyTable(table, afterCreate) putItem normalUser).withValue(user1: normalUser.Raw)
//       // (
//       //   ohnosequences.pointless.ops.typeSet.SerializeTo.cons,
//       //   // [SDKRep, Tagged[id.type], Tagged[name.type] :~: Tagged[email.type] :~: Tagged[color.type] :~: ∅],
//       //   implicitly[table.HashKey ∈ normalUser.Properties],
//       //   implicitly[table.RangeKey ∈ normalUser.Properties]
//       // )
//     // )
//     assert(putResul1.output === PutItemSuccess)
//     val afterPut1 = waitFor(table, putResul1.state)

//     // val putResul2 = service please (InCompositeKeyTable(table, afterPut1) putItem normalUser withValue user2)
//     val putResul2 = service please putputput(afterPut1, user2)
//     assert(putResul2.output === PutItemSuccess)
//     val afterPut2 = waitFor(table, putResul2.state)

//     val putResult3 = service please putputput(afterPut2, user3 as simpleUser)
//     assert(putResult3.output === PutItemSuccess)
//     val afterPut3 = waitFor(table, putResult3.state)

//     // QUERY TABLE

//     // here we get both users by the hash key
//     val simpleQueryResult = service please ((QueryTable(table, afterPut3) forItem normalUser).
//                                             withHashKey(user1.get(id))
//                                               // (
//                                               //   ToItem.buah(ToProperties.cons[
//                                               //     SDKRep, 
//                                               //     normalUser.properties.Head, normalUser.properties.Tail, 
//                                               //     normalUser.Raw, ???, 
//                                               //     fromSDKRep.type]
//                                               //   ),
//                                               //   implicitly[table.HashKey ∈ normalUser.Properties]
//                                               // ) 
//                                             )
//     assert(simpleQueryResult.output === QuerySuccess(List(user1, user2)))

//     // here we would get the same, but we add a range condition on the name
//     val normalQueryResult = service please (QueryTable(table, afterPut3) forItem normalUser
//                                             withHashKey user1.get(id) 
//                                             andRangeCondition (name beginsWith "Evd"))
//     assert(normalQueryResult.output === QuerySuccess(List(user2)))

//     // here we don't get anything
//     val emptyQueryResult = service please (QueryTable(table, afterPut3) forItem normalUser 
//                                             withHashKey user1.get(id) 
//                                             andRangeCondition (name beginsWith "foo"))
//     assert(emptyQueryResult.output === QuerySuccess(List()))

//     // TODO: change syntax to something nicer. maybe smth like this:
//     // (users, afterPut3) query normalUser hash 123 range (name beginsWith "my")

//     // GET ITEM
//     // NOTE: here we check that we can get a simpleUser instead of the normalUser and we will get only those properties
//     val getResult = service please (FromCompositeKeyTable(table, afterPut3) getItem simpleUser withKeys (user1.get(id), user1.get(name)))
//     assert(getResult.output === GetItemSuccess(
//       simpleUser fields ((id is 1) :~: (name is "Edu") :~: ∅)
//     ))

//     // DELETE ITEM + get again
//     val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterPut3) withKeys (user1.get(id), user1.get(name)))
//     val afterDel = waitFor(table, delResult.state)
//     val getResult2 = service please (FromCompositeKeyTable(table, afterDel) getItem normalUser withKeys (user1.get(id), user1.get(name)))
//     assert(getResult2.output === GetItemFailure("java.lang.NullPointerException"))
// =======

    val putResult1 = (service please PutItem(user1)).apply(afterCreate)
    assert(putResult1.output === PutItemSuccess)
    val afterPut1 = waitFor(table, putResult1.state)

    val putResult2 = (service please PutItem(user2)).apply(afterPut1)
    assert(putResult2.output === PutItemSuccess)
    val afterPut2 = waitFor(table, putResult2.state)

    val putResult3 = (service please PutItem(user3 as simpleUser)).apply(afterPut2)
    assert(putResult3.output === PutItemSuccess)
    val afterPut3 = waitFor(table, putResult3.state)

//     // // QUERY TABLE

//     // // here we get both users by the hash key
//     // val simpleQueryResult = service please (QueryTable(afterPut3) forItem normalUser 
//     //                                         withHashKey user1.get(id))
//     // assert(simpleQueryResult.output === QuerySuccess(List(user1, user2)))

//     // // here we would get the same, but we add a range condition on the name
//     // val normalQueryResult = service please (QueryTable(afterPut3) forItem normalUser
//     //                                         withHashKey user1.get(id) 
//     //                                         andRangeCondition (name beginsWith "Evd"))
//     // assert(normalQueryResult.output === QuerySuccess(List(user2)))

//     // // here we don't get anything
//     // val emptyQueryResult = service please (QueryTable(afterPut3) forItem normalUser 
//     //                                         withHashKey user1.get(id) 
//     //                                         andRangeCondition (name beginsWith "foo"))
//     // assert(emptyQueryResult.output === QuerySuccess(List()))

//     // // TODO: change syntax to something nicer. maybe smth like this:
//     // // (users, afterPut3) query normalUser hash 123 range (name beginsWith "my")

//     // // GET ITEM
//     // // NOTE: here we check that we can get a simpleUser instead of the normalUser and we will get only those attributes
//     // val getResult = service please (FromCompositeKeyTable(afterPut3) getItem simpleUser withKeys (user1.get(id), user1.get(name)))
//     // assert(getResult.output === GetItemSuccess(
//     //   simpleUser ->> ((id ->> 1) :~: (name ->> "Edu") :~: ∅)
//     // ))

//     // // DELETE ITEM + get again
//     // val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterPut3) withKeys (user1.get(id), user1.get(name)))
//     // val afterDel = waitFor(table, delResult.state)
//     // val getResult2 = service please (FromCompositeKeyTable(afterDel) getItem normalUser withKeys (user1.get(id), user1.get(name)))
//     // assert(getResult2.output === GetItemFailure("java.lang.NullPointerException"))
// >>>>>>> feature/table/ops

    // DELETE TABLE
    val lastState = waitFor(table, createResult.state)
    (service please DeleteTable(table)).apply(lastState)

  }

}
