package ohnosequences.tabula

trait AnyDeleteTable extends AnyTableAction {
  type InputState = Active[Table]
  type OutputState = Deleting[Table]

  type Input  = None.type
  val  input  = None
  type Output = None.type
}

case class DeleteTable[T <: Singleton with AnyTable]
  (table: T, inputState: Active[T])
    extends AnyDeleteTable { type Table = T }
