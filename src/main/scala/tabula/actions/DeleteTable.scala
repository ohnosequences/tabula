package ohnosequences.tabula.action

import ohnosequences.tabula._, states._, actions._, tables._


case class DeleteTable[T <: AnyTable](val table: T) extends AnyTableAction {

  type Table = T

  type InputState  = Active[Table]
  type OutputState = Deleting[Table]

  type Output = None.type
}
