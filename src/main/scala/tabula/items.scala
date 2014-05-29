package ohnosequences.tabula

import ohnosequences.scarph._

/*
  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/
trait AnyItemType {

  type TableType <: AnyTableType
  val tableType: TableType
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