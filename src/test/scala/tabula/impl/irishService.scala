package ohnosequences.tabula.impl

import org.scalatest.FunSuite

import com.amazonaws.regions._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue, AttributeAction}

import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._, AttributeImplicits._, DynamoDBExecutors._

import shapeless.test.{typed, illTyped}

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

  case object testItem extends Item(table, id :~: name :~: ∅)

  object testItemImplicits {
    implicit def getSDKRep(rep: testItem.Rep): Map[String, AttributeValue] = {
      import testItem._
      Map[String, AttributeValue](
        id.label -> getAttrVal(rep.get(id)),
        name.label -> getAttrVal(rep.get(name))
      )
    }

    implicit def parseSDKRep(m: Map[String, AttributeValue]): testItem.Rep = {
      testItem ->> (
        (id ->> m(id.label).getN.toInt) :~: 
        (name ->> m(name.label).getS.toString) :~:
        ∅
      )
    }
  }
}

class irishService extends FunSuite {
  import TestImplicits._
  import TestSetting._
  import testItemImplicits._


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
    { import shapeless._

      val wid = implicitly[Witness.Aux[id.type]]
      typed[id.type](wid.value)
      typed[wid.T](id)
      implicitly[wid.T =:= id.type]
      implicitly[wid.value.Raw =:= Int]
      assert(wid.value == id)
      
      val wname = implicitly[Witness.Aux[name.type]]
    }

    import AnyDenotation._

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

    import testItem._
    assert(i.get(id) === 123)
    assert(i.get(name) === "foo")

    // val keys = implicitly[Keys.Aux[i.type, id.type :~: name.type :~: ∅]]

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
    import testItem._
    val myItem = testItem ->> (
      (id ->> 123) :~: 
      (name ->> "myItem") :~: 
      ∅
    )

    val putResult = service please (InTable(table, afterCreate) putItem testItem withValue myItem)
    assert(putResult.output === PutItemSuccess)
    val afterPut = waitFor(table, putResult.state)

    // GET ITEM
    val getResult = service please (FromCompositeKeyTable(table, afterPut) getItem testItem withKeys (myItem.get(id), myItem.get(name)))
    assert(getResult.output === GetItemSuccess(myItem))


    //negative test
   // val updateResult = service please (FromCompositeKeyTable(table, afterPut) updateItem testItem withKeys (
   //   myItem.get(id), myItem.get(name), Map(id.label -> new AttributeValueUpdate(1, AttributeAction.ADD))))



    // DELETE ITEM + get again
    val delResult = service please (DeleteItemFromCompositeKeyTable(table, afterPut) withKeys (myItem.get(id), myItem.get(name)))
    val afterDel = waitFor(table, delResult.state)

    // prints exception stacktrace - it's ok
    val getResult2 = service please (FromCompositeKeyTable(table, afterDel) getItem testItem withKeys (myItem.get(id), myItem.get(name)))
    assert(getResult2.output === GetItemFailure())

    // DELETE TABLE
    val lastState = waitFor(table, getResult2.state)
    service please DeleteTable(table, lastState)

  }

  test("item test") {
    //itemTest

    val myid: Int = ohnosequences.tabula.impl.itemtest.TestItem.get(ohnosequences.tabula.impl.itemtest.id, (123, "123"))
    println(myid)
    assert(myid === 123)

    //build test

    val builder = ohnosequences.tabula.impl.itemtest.TestItem.builder()

    builder.addAttribute(ohnosequences.tabula.impl.itemtest.id)(123)
    builder.addAttribute(ohnosequences.tabula.impl.itemtest.name)("123")

    println(builder.result())

  }

}
