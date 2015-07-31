package ohnosequences.tabula


import ohnosequences.cosas._, typeSets._

/*
  ## Predicates

  Predicates represent expressions combining several conditions for a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Predicate constructors check that the item has the property used in the applied condition.
*/
trait AnyPredicate {

  type Body <: AnyPredicate
  val  body: Body

  type Head <: Condition
  val  head: Head

  type Item <: AnyItem
  val  item: Item
}

/*
  ### OR Predicates
*/
trait AnyOrPredicate extends AnyPredicate {

  type Body <: AnyOrPredicate

  def or[Other <: Condition](other: Other)(implicit
    ev: Other#Property ∈ Body#Item#Properties
  ): OR[Body, Other] =
     OR(body, other)
}

case class OR[B <: AnyOrPredicate, H <: Condition]
  (val body : B,  val head : H) extends AnyOrPredicate {
  type Body = B; type Head = H

  type Item = Body#Item
  val  item = body.item
}


/*
  ### AND Predicates
*/
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyAndPredicate

  def and[Other <: Condition](other: Other)(implicit
    ev: Other#Property ∈ Body#Item#Properties
  ): AND[Body, Other] =
     AND(body, other)
}

case class AND[B <: AnyAndPredicate, H <: Condition]
  (val body : B,  val head : H) extends AnyAndPredicate {

  type Body = B; type Head = H

  type Item = Body#Item
  val  item = body.item
}


/*
  ### Simple Predicates

  It contains only one condition and can be extended either to `OR` or `AND` predicate
*/
trait AnySimplePredicate extends AnyOrPredicate with AnyAndPredicate {

  type Me = this.type
  type Body = Me
  val  body = this: Me
}

case class SimplePredicate[I <: AnyItem, C <: Condition]
  (val item : I,  val head : C) extends AnySimplePredicate {
  type Item = I; type Head = C
}


object AnyPredicate {

  type HeadedBy[C <: Condition] = AnyPredicate { type Head <: C }

  type On[I <: AnyItem] = AnyPredicate { type Item = I }

  /*
    With this you can write `item ? condition` which means `SimplePredicate(item, condition)`
  */
  implicit def itemPredicateOps[I <: AnyItem](item: I): ItemPredicateOps[I] = ItemPredicateOps(item)
  case class   ItemPredicateOps[I <: AnyItem](val item: I) {
    def ?[C <: Condition](c: C)(implicit
        ev: C#Property ∈ I#Properties
      ): SimplePredicate[I, C] = SimplePredicate(item, c)
  }
}

/* An implicit check that this predicate consists only of KeyConditions */
sealed class OnlyWitnKeyConditions[P <: AnyPredicate]

object OnlyWitnKeyConditions extends OnlyWitnKeyConditions2 {
  implicit def simple[P <: AnySimplePredicate with AnyPredicate.HeadedBy[KeyCondition]]:
    OnlyWitnKeyConditions[P] = new OnlyWitnKeyConditions[P]
}

// This needs to be separated, otherwise implicits will deverge for SimplePredicate
trait OnlyWitnKeyConditions2 {

  implicit def and[P <: AnyAndPredicate with AnyPredicate.HeadedBy[KeyCondition]]
    (implicit ev: OnlyWitnKeyConditions[P#Body]):
                  OnlyWitnKeyConditions[P] =
              new OnlyWitnKeyConditions[P]

  implicit def  or[P <: AnyOrPredicate  with AnyPredicate.HeadedBy[KeyCondition]]
    (implicit ev: OnlyWitnKeyConditions[P#Body]):
                  OnlyWitnKeyConditions[P] =
              new OnlyWitnKeyConditions[P]
}
