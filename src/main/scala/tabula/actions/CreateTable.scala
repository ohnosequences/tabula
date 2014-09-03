package ohnosequences.tabula

trait AnyCreateTable extends AnyTableAction {

  type InputState = InitialState[Table]
  type OutputState = Creating[Table]

  type Input  = Option[Nothing]
  val  input  = None
  type Output = Option[Nothing]
}

// case class CreateTable[T <: AnyTable](val table: T, val inputState: InitialState[T])
//   extends AnyCreateTable { type Table = T }
case class CreateTable[T <: Singleton with AnyTable](table: T)
  extends AnyCreateTable { type Table = T }
