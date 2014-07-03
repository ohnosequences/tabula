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
import shapeless.test.{typed, illTyped}
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

  case object testItem extends Item(table, id :~: name :~: ∅)
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

  test("item attribute witnesses") {

    val wid = implicitly[Witness.Aux[id.type]]
    typed[id.type](wid.value)
    typed[wid.T](id)
    implicitly[wid.T =:= id.type]
    implicitly[wid.value.Raw =:= Int]
    assert(wid.value == id)
    
    val wname = implicitly[Witness.Aux[name.type]]


    val x = name ->> "foo"
    val y = implicitly[name.Rep => name.type]
    assert(y(x) == name)

    ///////

    implicitly[Represented.By[∅, ∅]]
    implicitly[Represented.By[id.type :~: name.type :~: ∅, TaggedWith[id.type] :~: TaggedWith[name.type] :~: ∅]] 
    implicitly[Represented.By[id.type :~: name.type :~: ∅, id.Rep :~: name.Rep :~: ∅]] 

    implicitly[testItem.Raw =:= (id.Rep :~: name.Rep :~: ∅)]
    implicitly[testItem.representedAttributes.Out =:= (id.Rep :~: name.Rep :~: ∅)]

    // creating item is easy and neat:
    val i = testItem ->> (
      (id ->> 123) :~: 
      (name ->> "foo") :~: 
      ∅
    )

    // you have to set _all_ attributes
    illTyped("""
    val wrongAttrSet = testItem ->> (
      (id ->> 123) :~: ∅
    )
    """)

    // and in the _fixed order_
    illTyped("""
    val wrongOrder = testItem ->> (
      (name ->> "foo") :~: 
      (id ->> 123) :~:
      ∅
    )
    """)

    assert(i.attr(id) === 123)
    assert(i.attr(name) === "foo")

    // val keys = implicitly[Keys.Aux[id.Rep :~: name.Rep :~: ∅, id.type :~: name.type :~: ∅]]
    val tags = TagsOf[id.Rep :~: name.Rep :~: ∅]
    assert(tags(i) === testItem.attributes)
    assert(tags(i) === (id :~: name :~: ∅))


    // transforming testItem to Map
    val tr = FromAttributes[
      id.type :~: name.type :~: ∅, 
      id.Rep  :~: name.Rep  :~: ∅,
      toSDKRep.type,
      SDKRep
    ]
    val map1 = tr(i)
    println(map1)

    val t = implicitly[FromAttributes.Aux[testItem.Attributes, testItem.Raw, toSDKRep.type, SDKRep]]
    val ti = implicitly[FromAttributes.ItemAux[testItem.type, toSDKRep.type, SDKRep]]
    val map2 = ti(i)
    println(map2)
    assert(map1 == map2)

    // forming testItem from Map
    val form = ToAttributes[SDKRep, testItem.Attributes, testItem.Raw, fromSDKRep.type](ToAttributes.cons)
    val i2 = form(map2, testItem.attributes)
    println(i2)
    assert(i2 == i)

  }

  test("complex example") {
    // CREATE TABLE
    val createResult = service please CreateTable(table, InitialState(table, service.account, InitialThroughput(1, 1)))
    val afterCreate = waitFor(table, createResult.state)

    // UPDATE TABLE (takes time)
    // val updateResult  = service please UpdateTable(table, afterCreate).withReadWriteThroughput(2, 2)
    // val afterUpdate = waitFor(table, updateResult.state)
    // val updateResult2 = service please UpdateTable(table, afterUpdate).withReadWriteThroughput(1, 1)
    // val afterUpdate2 = waitFor(table, updateResult2.state)

    // PUT ITEM
    // import testItem._
    val myItem = testItem ->> (
      (id ->> 123) :~: 
      (name ->> "myItem") :~: 
      ∅
    )

    // NOTE: don't know why these explicit import are needed..
    import toSDKRep._
    val putResult = service please (InTable(table, afterCreate) putItem testItem withValue myItem)
    assert(putResult.output === PutItemSuccess)
    val afterPut = waitFor(table, putResult.state)

    // GET ITEM
    import fromSDKRep._
    val getResult = service please (FromCompositeKeyTable(table, afterPut) getItem testItem withKeys (myItem.attr(id), myItem.attr(name)))
    assert(getResult.output === GetItemSuccess(myItem))

    // QUERY TABLE
    import Condition._

    val simpleQuery = (QueryTable(table, afterPut) forItem testItem withHashKey 123)
    // val normalQuery = QueryTable(table, afterDel) forItem testItem withHashKey 123 andRangeCondition (name beginsWith "my")

    val queryResult = service please simpleQuery
    println(queryResult.output)

    // DELETE ITEM + get again
    val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterPut) withKeys (myItem.attr(id), myItem.attr(name)))
    val afterDel = waitFor(table, delResult.state)
    val getResult2 = service please (FromCompositeKeyTable(table, afterDel) getItem testItem withKeys (myItem.attr(id), myItem.attr(name)))
    assert(getResult2.output === GetItemFailure("java.lang.NullPointerException"))

    // DELETE TABLE
    val lastState = waitFor(table, getResult2.state)
    service please DeleteTable(table, lastState)

  }

}
