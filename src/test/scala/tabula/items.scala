package ohnosequences.tabula

import org.scalatest.FunSuite

import ohnosequences.pointless._, AnyTaggedType.Tagged, AnyProperty._, AnyTypeSet._, AnyRecord._, AnyTypeUnion._

import ohnosequences.tabula._
import ohnosequences.tabula.impl._, ImplicitConversions._

import shapeless._, poly._
import shapeless.test.typed

object TestSetting {
  case object id extends Property[Num]
  case object name extends Property[String]
  case object simpleUserRecord extends Record(id :~: name :~: ∅)
  case object normalUserRecord extends Record(id :~: name :~: email :~: color :~: ∅)

  object table extends CompositeKeyTable("foo_table", id, name, EU)

  object simpleUser extends Item("simpleUser", table, id :~: name :~: ∅)
  object simpleUser2 extends Item("simpleUser2", table, simpleUserRecord.properties)

  // more properties:
  case object email extends Property[String]
  case object color extends Property[String]

  case object normalUser extends Item("normalUser", table, normalUserRecord.properties)

  // creating item is easy and neat:
  val user1 = simpleUser fields (
    (id is 123) :~: 
    (name is "foo") :~: 
    ∅
  )

  // this way the order of properties doesn't matter
  val user2 = normalUser fields (
    (name is "foo") :~: 
    (color is "orange") :~:
    (id is 123) :~: 
    (email is "foo@bar.qux") :~:
    ∅
  )

}

class itemsSuite extends FunSuite {
  import TestSetting._

  test("accessing item properties") {

    assert (

      user1.get(id) === 123
    )
    assert(user1.get(name) === "foo")
  }

  test("tags/keys of a representation") {
    // won't work; need the alias :-|
    // val keys = implicitly[Keys.Aux[id.Rep :~: name.Rep :~: ∅, id.type :~: name.type :~: ∅]]
    // val tags = TagsOf[Tagged[id.type] :~: Tagged[name.type] :~: ∅]
    // assert(tags(user1) === simpleUser.properties)
    // assert(tags(user1) === (id :~: name :~: ∅))
  }

  test("items serialization") {
    // transforming simpleUser to Map
    val tr = FromProperties[
      id.type :~: name.type :~: ∅, 
      Tagged[id.type]  :~: Tagged[name.type]  :~: ∅,
      toSDKRep.type,
      SDKRep
    ]
    val map1 = tr(user1)
    println(map1)

    val t = implicitly[FromProperties.Aux[simpleUser.Properties, simpleUser.Raw, toSDKRep.type, SDKRep]]
    val ti = implicitly[From.ItemAux[simpleUser.type, toSDKRep.type, SDKRep]]
    val map2 = ti(user1)
    println(map2)
    assert(map1 == map2)

    // forming simpleUser from Map
    val form = ToProperties[SDKRep, simpleUser.Properties, simpleUser.Raw, fromSDKRep.type](ToProperties.cons)
    val i2 = form(map2, simpleUser.properties)
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

    case object more extends Record(simpleUser.properties ∪ (email :~: color :~: ∅))

    case object extendedUser extends Item("extendedUser", table, more.properties)

    assertResult(user2) {
      user1 as (normalUser,
        (color is "orange") :~:
        (email is "foo@bar.qux") :~:
        ∅
      )
    }

    // you cannot provide less that it's missing
    assertTypeError("""
    val less = user1 as (normalUser,
        (email is "foo@bar.qux") :~:
        ∅
      )
    """)

    // neither you can provide more that was missing
    assertTypeError("""
    val more = user1 as (normalUser,
        (color is "orange") :~:
        (id is 4012) :~:
        (email is "foo@bar.qux") :~:
        ∅
      )
    """)
  }

  test("item update") {
    
    val martin = normalUser fields (
      (name is "Martin") :~:
      (id is 1) :~:
      (color is "dark-salmon") :~:
      (email is "coolmartin@scala.org") :~:
      ∅
    )

    assert(

      (
        
        user2 update (name is "qux")
      ) === (

        normalUser fields (
          (id is user2.get(id)) :~: 
          (name is "qux") :~: 
          (color is user2.get(color)) :~:
          (email is user2.get(email)) :~:
          ∅
        )
      )
    )

    assert((user2 update ((name is "qux") :~: (id is 42) :~: ∅)) === 
      (normalUser fields (
          (id is 42) :~: 
          (name is "qux") :~: 
          (color is user2.get(color)) :~:
          (email is user2.get(email)) :~:
          ∅
        ))
    )

    assert((user2 update (martin: normalUser.Raw)) === martin)
  }

}
