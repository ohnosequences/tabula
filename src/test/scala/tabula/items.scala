package ohnosequences.tabula

import org.scalatest.FunSuite

import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._, ImplicitConversions._

import shapeless._, poly._
import shapeless.test.typed
import AnyTag._

object TestSetting {
  case object id extends Attribute[Int]
  case object name extends Attribute[String]

  object table extends CompositeKeyTable("foo_table", id, name, EU)

  case object simpleUser extends Item(table, id :~: name :~: ∅)

  // more attributes:
  case object email extends Attribute[String]
  case object color extends Attribute[String]

  case object normalUser extends Item(table, id :~: name :~: email :~: color :~: ∅)

  // creating item is easy and neat:
  val user1 = simpleUser ->> (
    (id ->> 123) :~: 
    (name ->> "foo") :~: 
    ∅
  )

  // with the := operator, the order of attributes doesn't matter
  val user2 = normalUser ~ (
    (name ~ "foo") :~: 
    (color ~ "orange") :~:
    (id ~ 123) :~: 
    (email ~ "foo@bar.qux") :~:
    ∅
  )

}

class itemsSuite extends FunSuite {
  import TestSetting._

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
  }

  test("representing attribute sets") {

    implicitly[Represented.By[∅, ∅]]
    implicitly[Represented.By[id.type :~: name.type :~: ∅, TaggedWith[id.type] :~: TaggedWith[name.type] :~: ∅]] 
    implicitly[Represented.By[id.type :~: name.type :~: ∅, id.Rep :~: name.Rep :~: ∅]] 

    implicitly[simpleUser.Raw =:= (id.Rep :~: name.Rep :~: ∅)]
    implicitly[simpleUser.representedAttributes.Out =:= (id.Rep :~: name.Rep :~: ∅)]
  }

  test("invalid item values") {
    // you have to set _all_ attributes
    assertTypeError("""
    val wrongAttrSet = simpleUser ->> (
      (id ->> 123) :~: ∅
    )
    """)

    // and in the _fixed order_
    assertTypeError("""
    val wrongOrder = simpleUser ->> (
      (name ->> "foo") :~: 
      (id ->> 123) :~:
      ∅
    )
    """)

    // but you still have to present all attributes:
    assertTypeError("""
    val wrongAttrSet = normalUser ~ (
      (id ->> 123) :~:
      (name ->> "foo") :~: 
      ∅
    )
    """)
  }

  test("accessing item attributes") {
    assert(user1.attr(id) === 123)
    assert(user1.attr(name) === "foo")
  }

  test("tags/keys of a representation") {
    // val keys = implicitly[Keys.Aux[id.Rep :~: name.Rep :~: ∅, id.type :~: name.type :~: ∅]]
    val tags = TagsOf[id.Rep :~: name.Rep :~: ∅]
    assert(tags(user1) === simpleUser.attributes)
    assert(tags(user1) === (id :~: name :~: ∅))
  }

  test("items serializaion") {
    // transforming simpleUser to Map
    val tr = FromAttributes[
      id.type :~: name.type :~: ∅, 
      id.Rep  :~: name.Rep  :~: ∅,
      toSDKRep.type,
      SDKRep
    ]
    val map1 = tr(user1)
    println(map1)

    val t = implicitly[FromAttributes.Aux[simpleUser.Attributes, simpleUser.Raw, toSDKRep.type, SDKRep]]
    val ti = implicitly[FromAttributes.ItemAux[simpleUser.type, toSDKRep.type, SDKRep]]
    val map2 = ti(user1)
    println(map2)
    assert(map1 == map2)

    // forming simpleUser from Map
    val form = ToAttributes[SDKRep, simpleUser.Attributes, simpleUser.Raw, fromSDKRep.type](ToAttributes.cons)
    val i2 = form(map2, simpleUser.attributes)
    println(i2)
    assert(i2 == user1)
  }

  test("item projection") {
    assertResult(user1) {
      user2 as simpleUser
    }

    assertTypeError("""
    val wrong = user1 as normalUser
    """)
  }

  test("item extension") {
    val more = simpleUser.attributes U (email :~: color :~: ∅)
    case object extendedUser extends Item(table, more)

    assertResult(user2) {
      user1 as (normalUser,
        (color ~ "orange") :~:
        (email ~ "foo@bar.qux") :~:
        ∅
      )
    }

    // you cannot provide less that it's missing
    assertTypeError("""
    val less = user1 as (normalUser,
        (email ~ "foo@bar.qux") :~:
        ∅
      )
    """)

    // neither you can provide more that was missing
    assertTypeError("""
    val more = user1 as (normalUser,
        (color ~ "orange") :~:
        (id ~ 4012) :~:
        (email ~ "foo@bar.qux") :~:
        ∅
      )
    """)
  }

}
