package ohnosequences.tabula

import ohnosequences.scarph._


/*
  ## Predicates

  Predicates represent complex expressions using multiple conditions on a particular item.
  You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
  Predicate constructors check that the item has the attribute used in the applied condition.
*/
trait AnyPredicate {

  type Item <: Singleton with AnyItem
  val  item: Item
}

/* ### OR Predicates */
trait AnyOrPredicate extends AnyPredicate {

  def or[Other <: Condition](other: Other)(implicit 
    ev: Item HasProperty other.Attribute
  ): OR[this.type, other.type] = OR[this.type, other.type](this, other)
}

case class OR[P <: AnyOrPredicate, C <: Condition](val allThis: P, val also: C) 
  extends AnyOrPredicate { 

  type Item = P#Item
  val item = allThis.item
} 


/* ### AND Predicates */
trait AnyAndPredicate extends AnyPredicate {

  def and[Other <: Condition](other: Other)(implicit 
    ev: Item HasProperty other.Attribute
  ): AND[this.type, other.type] = AND[this.type, other.type](this, other)
}

case class AND[P <: AnyAndPredicate, C <: Condition](val allThis: P, val also: C) 
  extends AnyAndPredicate { 

  type Item = P#Item
  val item = allThis.item 
}


/* ### Initial Predicate */
case class SimplePredicate[I <: Singleton with AnyItem, C <: Condition]
  (val item: I, val condition: C) 
    extends AnyOrPredicate with AnyAndPredicate { type Item = I }


object AnyPredicate {

  type On[I <: AnyItem] = AnyPredicate { type Item = I }

  /* 
    With this you can write `item ? condition` which means `SimplePredicate(item, condition)`
  */
  implicit def toItemPredicateOps[I <: Singleton with AnyItem](item: I): ItemPredicateOps[I] = ItemPredicateOps(item)
  case class ItemPredicateOps[I <: Singleton with AnyItem](item: I) {
    def ?[C <: Condition](c: C)(implicit 
        ev: I HasProperty C#Attribute
      ): SimplePredicate[I, C] =
         SimplePredicate(item, c)
  }
}
