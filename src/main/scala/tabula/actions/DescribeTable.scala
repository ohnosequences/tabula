package ohnosequences.tabula

trait AnyDescribeTable extends AnyTableAction {
  type InputState  = AnyTableState.For[Table]
  type OutputState = AnyTableState.For[Table]

  type Input  = Option[Nothing]
  val  input  = None
  type Output = Option[Nothing]
}

case class DescribeTable[T <: AnyTable]
  (val table: T, val inputState: AnyTableState.For[T]) 
    extends AnyDescribeTable { type Table = T }
