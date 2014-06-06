package ohnosequences.tabula

import ohnosequences.scarph._

/*
  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/
trait AnyItemType {

  type Table <: AnyTable
  val table: Table
  // just in case
  type Key = Table#Key
}


object AnyItemType {

  type of[T <: AnyTable] = AnyItemType { type Table = T }
  implicit def itemTypeOps[IT <: AnyItemType](itemType: IT) = ItemTypeOps(itemType)
}

class ItemType[T <: AnyTable](val table: T) extends AnyItemType {

  type Table = T
}

case class ItemTypeOps[IT <: AnyItemType](val itemType: IT) {

  def has[P <: AnyAttribute](p: P) = HasProperty[IT, P](itemType, p)
}



/*
  Items are denotations of an item type. the table type is accessible through the item type.
*/
trait AnyItem extends Denotation[AnyItemType] with HasProperties {}