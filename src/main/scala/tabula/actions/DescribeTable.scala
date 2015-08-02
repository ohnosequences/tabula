package ohnosequences.tabula.action

import ohnosequences.tabula._, states._, actions._, tables._


case class DescribeTable[T <: AnyTable](val table: T) extends AnyTableAction {

  type Table = T

  type InputState  = AnyTableState.For[Table]
  type OutputState = AnyTableState.For[Table]

  type Output = None.type
}
