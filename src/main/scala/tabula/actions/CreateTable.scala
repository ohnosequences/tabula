package ohnosequences.tabula

trait AnyCreateTable extends AnyTableAction {
  type InputState = InitialState[Table]
  type OutputState = Creating[Table]

  type Input  = None.type
  val  input  = None
  type Output = None.type
}

case class CreateTable[T <: Singleton with AnyTable]
  (table: T, inputState: InitialState[T])
    extends AnyCreateTable { type Table = T }
