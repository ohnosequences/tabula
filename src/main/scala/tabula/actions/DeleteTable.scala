package ohnosequences.tabula

trait AnyDeleteTable extends AnyTableAction {
  type InputState = Active[Table]
  type OutputState = Deleting[Table]

  type Input  = Option[Nothing]
  val  input  = None
  type Output = Option[Nothing]
}

case class DeleteTable[T <: AnyTable]
  (val table: T, val inputState: Active[T])
    extends AnyDeleteTable { type Table = T }
