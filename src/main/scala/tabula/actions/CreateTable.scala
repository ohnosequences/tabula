package ohnosequences.tabula.action

import ohnosequences.tabula._, states._, actions._, tables._


case class CreateTable[T <: AnyTable](val table: T) extends AnyTableAction {

  type Table = T

  type InputState  = InitialState[Table]
  type OutputState = Creating[Table]

  type Output = None.type
}
