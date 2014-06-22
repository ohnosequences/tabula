package ohnosequences.tabula

import ohnosequences.scarph._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait AnyItem extends Representable with CanGetPropertiesOfItself {
  val label: String

  /* The table is accessible through the item type */
  type Table <: AnyTable
  val  table: Table
}

class Item[T <: AnyTable](val table: T) extends AnyItem { 
  val label = this.toString

  type Table = T
}

object AnyItem {
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }

  implicit def itemOps[I <: AnyItem](item: I): ItemOps[I] = ItemOps[I](item)
  case class   ItemOps[I <: AnyItem](item: I) extends HasPropertiesOps(item)
}
