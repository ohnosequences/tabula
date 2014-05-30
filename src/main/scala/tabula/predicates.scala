package ohnosequences.tabula

import ohnosequences.scarph._


/*
  This is more or less OK by now.
*/
trait AnyPredicate {

  type ItemType <: Singleton with AnyItemType
  val itemType: ItemType
}

trait AndPredicate extends AnyPredicate { self =>

  type ItemType <: Singleton with AnyItemType

  def and[Other <: Condition](other: Other)
    (implicit ev: ItemType HasProperty other.Attribute): AndPredicate { type ItemType = self.ItemType }
}
trait OrPredicate extends AnyPredicate

case class AND[P <: Singleton with AndPredicate, C <: Condition](val allThis: P, val also: C) 
extends AndPredicate { self =>

  type ItemType = allThis.ItemType
  val itemType = allThis.itemType

  def and[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): AndPredicate { type ItemType = self.ItemType } = AND[this.type, Other](this, other)
}

// an atomic pred; it is both or, and
case class SimplePredicate[I <: Singleton with AnyItemType, C <: Condition](val itemType: I, val condition: C) 
extends AnyPredicate with AndPredicate with OrPredicate { self =>

  type ItemType = I

  def and[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): AndPredicate { type ItemType = self.ItemType } = AND[this.type, Other](this, other)
}

case class PredicateOn[I <: Singleton with AnyItemType](val itemType: I)
extends AnyPredicate with AndPredicate with OrPredicate { self =>

  type ItemType = I

  def and[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): AndPredicate { type ItemType = self.ItemType } = AND[this.type, Other](this, other)

  def ?[C <: Condition](c: C)(implicit ev: ItemType HasProperty C#Attribute): SimplePredicate[ItemType,C] =
    SimplePredicate(itemType, c)
}

  
object AnyPredicate {

  type Over[I <: AnyItemType] = AnyPredicate { type ItemType = I }

  implicit def toPredicateOps[I <: Singleton with AnyItemType](itemType: I): PredicateOn[I] = PredicateOn(itemType)
}