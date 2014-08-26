package ohnosequences.tabula



trait AnyUpdateTableAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = Updating[Table]

  type ReadThroughput  = Int
  type WriteThroughput = Int
  val  newReadThroughput: ReadThroughput
  val  newWriteThroughput: WriteThroughput

  type Input = (ReadThroughput, WriteThroughput)
  val  input = (newReadThroughput, newWriteThroughput)

  type Output = None.type
}

case class UpdateTable[T <: AnyTable](
  t: T, inputSt: AnyTableState.For[T] with ReadyTable) {
    case class withReadWriteThroughput(
      newReadThroughput: Int, 
      newWriteThroughput: Int
    ) extends AnyUpdateTableAction {
      type Table = T 
      val  table = t

      val inputState = inputSt
    }
  }
