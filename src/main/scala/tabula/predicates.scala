package ohnosequences.tabula

import ohnosequences.scarph._


/*
  This is more or less OK by now.
*/
trait AnyPredicate {

  type ItemType <: AnyItemType
  val itemType: ItemType
}

trait AndPredicate extends AnyPredicate {

  def and[Other <: Condition](other: Other)(implicit ev: ItemType HasProperty Other#Attribute): AndPredicate
}
trait OrPredicate extends AnyPredicate

case class AND[P <: AndPredicate, C <: Condition](val allThis: P, val also: C) extends AndPredicate {

  type ItemType = P#ItemType
  val itemType = allThis.itemType

  def and[Other <: Condition](other: Other)(implicit ev: ItemType HasProperty Other#Attribute): AndPredicate = 
    AND(this, other)
}

// an atomic pred; it is both or, and
case class SimplePredicate[I <: AnyItemType, C <: Condition](val itemType: I, val condition: C) 
extends AnyPredicate with AndPredicate with OrPredicate {

  type ItemType = I

  def and[Other <: Condition](other: Other)(implicit ev: ItemType HasProperty Other#Attribute): AndPredicate = 
  AND(this, other)
  }

case class PredicateOn[I <: AnyItemType](val itemType: I) extends AnyPredicate with AndPredicate with OrPredicate {

  type ItemType = I

  def and[Other <: Condition](other: Other)(implicit ev: ItemType HasProperty Other#Attribute): AndPredicate = 
    AND(this, other)

  def ?[C <: Condition](c: C)(implicit ev: ItemType HasProperty C#Attribute): SimplePredicate[ItemType,C] =
    SimplePredicate(itemType, c)
}

  
object AnyPredicate {

  type Over[I <: AnyItemType] = AnyPredicate { type ItemType = I }

  implicit def toPredicateOps[I <: AnyItemType](itemType: I): PredicateOn[I] = PredicateOn(itemType)
}