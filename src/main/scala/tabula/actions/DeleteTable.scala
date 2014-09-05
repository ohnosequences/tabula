package ohnosequences.tabula

trait AnyDeleteTable extends AnyTableAction {
  type InputState  <: Active[Table]
  type OutputState <: Deleting[Table]

  type Output = None.type
}

case class DeleteTable[T <: AnyTable](table: T) extends AnyDeleteTable {
  type Table = T

  type InputState  = Active[T]
  type OutputState = Deleting[T]
}
