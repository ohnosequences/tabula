package ohnosequences.tabula

import ohnosequences.scarph._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.

  ### items and keys

  The primary key of an item is accessible through the corresponding table
*/

/*
  Items are denotations of an item type. the table is accessible through the item type.
*/
trait AnyItem extends AnyDenotation with PropertyGetters {
  type Table <: AnyTable
  val  table: Table

  val label: String
  type TYPE <: AnyItem
}

class Item[T <: AnyTable](val table: T) extends Denotation[AnyItem] with AnyItem { 
  type Table = T

  val label = this.toString

  /* Item denotes itself */
  type Tpe = this.type
  val  tpe = this: Tpe
}

object AnyItem {
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
  // type RepOf[I <: Singleton with AnyItem] = AnyDenotation.TaggedWith[I]
  // type Rep = AnyDenotation.AnyTag { type Denotation <: AnyItem }

  implicit def itemOps[I <: AnyItem](item: I): ItemOps[I] = ItemOps[I](item)
}

case class ItemOps[I <: AnyItem](item: I) extends HasPropertiesOps(item) {}
