package ohnosequences.tabula

import ohnosequences.scarph._


/*
  ## Predicates

  Predicates represent complex expressions using multiple conditions on a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Predicate constructors check that the item has the attribute used in the applied condition.
*/
trait AnyPredicate {
  type Self <: AnyPredicate
  val  self: Self

  type Item <: Singleton with AnyItem
  val  item: Item
}

/* ### OR Predicates */
trait AnyOrPredicate extends AnyPredicate {

  type Self <: AnyOrPredicate

  def or[Other <: Condition](other: Other)(implicit 
    ev: self.Item HasProperty other.Attribute
  ): OR[Self, Other] = 
     OR(self, other)
}

case class OR[P <: AnyOrPredicate, C <: Condition]
  (val self : P, val other: C) extends AnyOrPredicate {
  type Self = P

  type Item = self.Item
  val  item = self.item
} 


/* ### AND Predicates */
trait AnyAndPredicate extends AnyPredicate {

  type Self <: AnyAndPredicate

  def and[Other <: Condition](other: Other)(implicit 
    ev: self.Item HasProperty other.Attribute
  ): AND[Self, Other] = 
     AND(self, other)
}

case class AND[P <: AnyAndPredicate, C <: Condition]
  (val self : P, val other: C) extends AnyAndPredicate {
  type Self = P

  type Item = self.Item
  val  item = self.item 
}


/* ### Initial Predicate */
case class SimplePredicate[I <: Singleton with AnyItem, C <: Condition]
  (val item: I, val condition: C) 
    extends AnyOrPredicate with AnyAndPredicate { type Item = I 

  type Self = this.type
  val  self = this: this.type
}


object AnyPredicate {

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
