package ohnosequences.tabula

import states._, actions._, tables._

trait AnyDescribeTable extends AnyTableAction {
  type InputState  = AnyTableState.For[Table]
  type OutputState = AnyTableState.For[Table]

  type Output = None.type
}

case class DescribeTable[T <: AnyTable](val table: T)
  extends AnyDescribeTable { type Table = T }
