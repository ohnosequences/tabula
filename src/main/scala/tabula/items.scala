package ohnosequences.tabula

import ohnosequences.scarph._

/*
  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/
trait AnyItemType { itemT =>

  type TableType <: AnyTableType
  val tableType: TableType
  // just in case
  type Key = TableType#Key

  /*
    predicates refer to this item type. They need to use attributes that this item type has; this is enforced when building them by requiring an implicit for the corresponding item having the attribute that the condition refers to. 

    All this stuff is nested here but could possibly be somewhere else just by adding an ItemType type/value pair to Predicate. In that case all the predicate builders should check that they are building predicates for the same item (or nest the classes inside the corresponding predicateOps trait/class)
  */
  trait Predicate

    trait AndPredicate extends Predicate

      case class AND[P <: AndPredicate, C <: Condition](
        val allThis: P, 
        val also: C
      )(implicit
        ev: (itemT.type HasProperty C#Attribute)
      ) 
      extends AndPredicate
    trait EMPTY extends AndPredicate
    object EMPTY_ extends EMPTY
    val EMPTY: EMPTY = EMPTY_

    trait OrPredicate extends Predicate

    trait PredicateOver[P <: Predicate, A <: AnyAttribute]

}


object AnyItemType {

  type of[T <: AnyTableType] = AnyItemType { type TableType = T }
  implicit def itemTypeOps[VT <: AnyItemType](et: VT) = ItemTypeOps(et)
}
class ItemType[T <: AnyTableType](val tableType: T) extends AnyItemType {

  type TableType = T
}

case class ItemTypeOps[IT <: AnyItemType](val vi: IT) {
  def has[P <: AnyAttribute](p: P) = HasProperty[IT, P](vi, p)
}

/*
  Items are denotations of an item type. the table type is accessible through the item type.
*/
trait AnyItem extends Denotation[AnyItemType] with HasProperties {}