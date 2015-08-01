package ohnosequences.tabula.action

import ohnosequences.tabula._, states._, actions._, tables._

trait AnyCreateTable extends AnyTableAction {

  type InputState  = InitialState[Table]
  type OutputState = Creating[Table]

  type Output = None.type
}

case class CreateTable[T <: AnyTable](val table: T)
  extends AnyCreateTable { type Table = T }
