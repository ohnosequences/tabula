package ohnosequences.tabula.action

import ohnosequences.tabula._, states._, actions._, tables._

trait AnyDeleteTable extends AnyTableAction {
  type InputState  = Active[Table]
  type OutputState = Deleting[Table]

  type Output = None.type
}

case class DeleteTable[T <: AnyTable](table: T)
  extends AnyDeleteTable { type Table = T }
