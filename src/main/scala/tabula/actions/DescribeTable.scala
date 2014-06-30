package ohnosequences.tabula

trait AnyDescribeTable extends AnyTableAction {
  type InputState  = AnyTableState.For[Table]
  type OutputState = AnyTableState.For[Table]

  type Input  = None.type
  val  input  = None
  type Output = None.type
}

case class DescribeTable[T <: Singleton with AnyTable]
  (table: T, inputState: AnyTableState.For[T]) 
    extends AnyDescribeTable { type Table = T }
