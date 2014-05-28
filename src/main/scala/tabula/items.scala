package ohnosequences.tabula

import ohnosequences.scarph._

// a table could possibly hold different kinds of records
trait AnyItemType {

  type TableType <: AnyTableType
  val tableType: TableType
}

/*
  Items are denotations of an item type. the table is accessible through the item type.
*/
trait AnyItem extends Denotation[AnyItemType] with HasProperties {}