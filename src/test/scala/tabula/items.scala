package ohnosequences.tabula

import org.scalatest.FunSuite

import ohnosequences.cosas._, types._, properties._, typeSets._, records._, typeUnions._

import ohnosequences.tabula._
import ohnosequences.tabula.impl._, ImplicitConversions._

import shapeless._, poly._
import shapeless.test.typed

object TestSetting {
  case object id extends Property[Num]("id")
  case object name extends Property[String]("name")
  case object simpleUserRecord extends Record(id :~: name :~: ∅)
  case object normalUserRecord extends Record(id :~: name :~: email :~: color :~: ∅)

  object table extends Table("foo_table", CompositeKey(id, name), EU)

  object simpleUser extends Item("simpleUser", table, id :~: name :~: ∅)
  object simpleUser2 extends Item("simpleUser2", table, simpleUserRecord.properties)

  // more properties:
  case object email extends Property[String]("email")
  case object color extends Property[String]("color")

  case object normalUser extends Item("normalUser", table, normalUserRecord.properties)

  // creating item is easy and neat:
  val user1 = simpleUser(
    id(123) :~:
    name("foo") :~:
    ∅
  )

  // this way the order of properties doesn't matter
  val user2 = normalUser(
    name("foo") :~:
    color("orange") :~:
    id(123) :~:
    email("foo@bar.qux") :~:
    ∅
  )

}

class itemsSuite extends FunSuite {
  import TestSetting._

  test("accessing item properties") {

    assert{ user1.get(id).value == 123 }
    assert{ user1.get(name).value == "foo" }
  }

  test("tags/keys of a representation") {
    // won't work; need the alias :-|
    // val keys = implicitly[Keys.Aux[id.Rep :~: name.Rep :~: ∅, id.type :~: name.type :~: ∅]]
    // val tags = TagsOf[ValueOf[id.type] :~: ValueOf[name.type] :~: ∅]
    // assert(tags(user1) == simpleUser.properties)
    // assert(tags(user1) == (id :~: name :~: ∅))
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
        color("orange") :~:
        email("foo@bar.qux") :~:
        ∅
      )
    }

    // you cannot provide less that it's missing
    assertTypeError("""
    val less = user1 as (normalUser,
        email("foo@bar.qux") :~:
        ∅
      )
    """)

    // neither you can provide more that was missing
    assertTypeError("""
    val more = user1 as (normalUser,
        color("orange") :~:
        id(4012) :~:
        email("foo@bar.qux") :~:
        ∅
      )
    """)
  }

  test("item update") {

    val martin = normalUser(
      name("Martin") :~:
      id(1) :~:
      color("dark-salmon") :~:
      email("coolmartin@scala.org") :~:
      ∅
    )

    assert(

      (

        user2 update name("qux")
      ) == (

        normalUser(
          id(user2.get(id).value) :~:
          name("qux") :~:
          color(user2.get(color).value) :~:
          email(user2.get(email).value) :~:
          ∅
        )
      )
    )

    assert((user2 update (name("qux") :~: id(42) :~: ∅)) ==
      (normalUser(
          id(42) :~:
          name("qux") :~:
          color(user2.get(color).value) :~:
          email(user2.get(email).value) :~:
          ∅
        ))
    )

    assert((user2 update martin.value) == martin)
  }

}
