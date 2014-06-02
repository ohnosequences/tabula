package ohnosequences.tabula

import ohnosequences.scarph._


/*
  This is more or less OK by now.
*/
trait AnyPredicate {

  type ItemType <: Singleton with AnyItemType
  val itemType: ItemType
}

class PredicateOn[I <: Singleton with AnyItemType](val itemType: I) 
  extends AnyPredicate { self =>

  type ItemType = I 

  def and[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): AND[this.type, Other] = AND[this.type, Other](this, other)

  def or[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): OR[this.type, Other] = OR[this.type, Other](this, other)
}

case class SimplePredicate[I <: Singleton with AnyItemType, C <: Condition](override val itemType: I, val condition: C) 
  extends PredicateOn[I](itemType) 

case class AND[P <: AnyPredicate, C <: Condition](val allThis: P, val also: C) 
  extends PredicateOn[P#ItemType](allThis.itemType) 

case class OR[P <: AnyPredicate, C <: Condition](val allThis: P, val also: C) 
  extends PredicateOn[P#ItemType](allThis.itemType) 


object AnyPredicate {

  type On[I <: AnyItemType] = AnyPredicate { type ItemType = I }

  // itemType ? condition == SimplePredicate(itemType, condition)
  implicit def toItemPredicateOps[I <: Singleton with AnyItemType](itemType: I): ItemPredicateOps[I] = ItemPredicateOps(itemType)
  case class ItemPredicateOps[I <: Singleton with AnyItemType](itemType: I) {
    def ?[C <: Condition](c: C)(implicit 
        ev: I HasProperty C#Attribute
      ): SimplePredicate[I, C] =
         SimplePredicate(itemType, c)
  }
}
