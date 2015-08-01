package ohnosequences.tabula

case object items {

  import attributes._, tables._
  import ohnosequences.cosas._, typeSets._, records._

  /*
    ## Items

    This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
  */

  // trait HasValidRaw extends TypePredicate[AnyType] {
  //   type Condition[T <: AnyType] = T#Raw isOneOf ValidValues
  // }

  trait AnyItem extends AnyRecord {

    type Attributes <: AnyTypeSet.Of[AnyAttribute]
    val  attributes: Attributes

    // From AnyRecord:
    type Properties = Attributes
    lazy val properties = attributes

    type Table <: AnyTable
    val  table: Table

    type Raw <: AnyTypeSet
  }

  class Item [
    T  <: AnyTable,
    Attrs <: AnyTypeSet.Of[AnyAttribute],
    Vals <: AnyTypeSet
  ](val label: String,
    val table: T,
    val attributes: Attrs
  )(implicit
    val valuesOfProperties: Vals areValuesOf Attrs
  ) extends AnyItem {

    type Attributes = Attrs
    type Raw = Vals
    type Table = T
  }

  type TableOf[I <: AnyItem] = I#Table
  def tableOf[I <: AnyItem](i: I): TableOf[I] = i.table
  object AnyItem {

    type ofTable[T <: AnyTable] = AnyItem { type Table = T }
    type ofCompositeTable = AnyItem { type Table <: AnyTable.withCompositeKey }
    // type withAttributes[A <: AnyTypeSet.Of[AnyAttribute]] = AnyItem { type Attributes = A }
  }

}
