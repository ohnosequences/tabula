package ohnosequences.tabula

import ohnosequences.scarph._


/*
  ## Predicates

  Predicates represent expressions combining several conditions for a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Predicate constructors check that the item has the attribute used in the applied condition.
*/
trait AnyPredicate {
  type Body <: AnyPredicate
  val  body: Body

  type Head <: Condition
  val  head: Head

  type Item <: Singleton with AnyItem
  val  item: Item
}

/*
  ### OR Predicates
*/
trait AnyOrPredicate extends AnyPredicate {

  type Body <: AnyOrPredicate

  def or[Head <: Condition](other: Head)(implicit 
    ev: body.Item HasProperty other.Attribute
  ): OR[Body, Head] = 
     OR(body, other)
}

case class OR[B <: AnyOrPredicate, H <: Condition]
  (val body : B,  val head : H) extends AnyOrPredicate {
  type Body = B; type Head = H

  type Item = body.Item
  val  item = body.item
} 


/* 
  ### AND Predicates
*/
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyAndPredicate

  def and[Head <: Condition](other: Head)(implicit 
    ev: body.Item HasProperty other.Attribute
  ): AND[Body, Head] = 
     AND(body, other)
}

case class AND[B <: AnyAndPredicate, H <: Condition]
  (val body : B,  val head : H) extends AnyAndPredicate {
  type Body = B; type Head = H

  type Item = body.Item
  val  item = body.item 
}


/* 
  ### Simple Predicates

  It contains only one condition and can be extended either to `OR` or `AND` predicate
*/
trait AnySimplePredicate extends AnyOrPredicate with AnyAndPredicate {
  type Body = this.type
  val  body = this: this.type
}

case class SimplePredicate[I <: Singleton with AnyItem, C <: Condition]
  (val item : I,  val head : C) extends AnySimplePredicate {
  type Item = I; type Head = C
}


object AnyPredicate {

  type HeadedBy[C <: Condition] = AnyPredicate { type Head <: C }

  type On[I <: AnyItem] = AnyPredicate { type Item = I }

  /* 
    With this you can write `item ? condition` which means `SimplePredicate(item, condition)`
  */
  implicit def itemPredicateOps[I <: Singleton with AnyItem](item: I): ItemPredicateOps[I] = ItemPredicateOps(item)
  case class   ItemPredicateOps[I <: Singleton with AnyItem](item: I) {
    def ?[C <: Condition](c: C)(implicit 
        ev: item.type HasProperty c.Attribute
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
