package ohnosequences.tabula

import ohnosequences.pointless._, AnyType._, AnyTypeSet._, AnyFn._, AnyTypeUnion._
import ohnosequences.pointless.ops.typeSet._
import shapeless._, poly._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait HasValidRaw extends TypePredicate[AnyType] {
  type Condition[T <: AnyType] = RawOf[T] isOneOf ValidValues
}

trait AnyItem extends AnyRecord {

  type Table <: AnyTable
  val  table: Table

  val validValues: Check[Properties, HasValidRaw]

  type Raw <: AnyTypeSet
}

class Item [
  T  <: AnyTable,
  Props <: AnyTypeSet.Of[AnyProperty],
  Vals <: AnyTypeSet
](val label: String,
  val table: T,
  val properties: Props
)(implicit
  val valuesOfProperties: Vals areValuesOf Props,
  val validValues: Check[Props, HasValidRaw]
) extends AnyItem {

  type Properties = Props
  type Raw = Vals
  type Table = T
}

object AnyItem {

  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
  type withProperties[P <: AnyTypeSet with AnyTypeSet.Of[AnyProperty]] = AnyItem { type Props = P }

  type TableOf[I <: AnyItem] = I#Table

  type OfCompositeTable = AnyItem { type Table <: AnyTable.withCompositeKey }
}
