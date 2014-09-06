package ohnosequences.tabula

trait AnyUpdateTable extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = Updating[Table]

  val newReadThroughput: Int
  val newWriteThroughput: Int

  type Output = None.type
}

case class UpdateTable[T <: AnyTable](val table: T)(
  val newReadThroughput: Int, 
  val newWriteThroughput: Int
) extends AnyUpdateTable { type Table = T }
