package ohnosequences.tabula

trait AnyDescribeTable extends AnyTableAction {
  type InputState  <: AnyTableState.For[Table]
  type OutputState <: AnyTableState.For[Table]

  type Output = None.type
}

case class DescribeTable[T <: AnyTable](t: T) extends TableAction[T](t) with AnyDescribeTable {
  // type Table = T

  type InputState  = AnyTableState.For[T]
  type OutputState = AnyTableState.For[T]
}
