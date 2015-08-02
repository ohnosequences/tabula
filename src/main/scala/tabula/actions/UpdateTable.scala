package ohnosequences.tabula.action

import ohnosequences.tabula._, states._, actions._, tables._

case class UpdateTable[T <: AnyTable](val table: T)(
  val newReadThroughput: Int,
  val newWriteThroughput: Int
) extends AnyTableAction {

  type Table = T

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = Updating[Table]

  type Output = None.type
}
