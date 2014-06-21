package ohnosequences.tabula

import ohnosequences.scarph._

trait AnyUpdateTable extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = Updating[Table]

  type NewReadThroughput  = Int
  type NewWriteThroughput = Int
  type Input = (NewReadThroughput, NewWriteThroughput)

  type Output = None.type
}

case class UpdateTable[T <: Singleton with AnyTable](
  t: T, inputSt: AnyTableState.For[T] with ReadyTable) {
    case class withReadWriteThroughput(
      newReadThroughput: Int, 
      newWriteThroughput: Int
    ) extends AnyUpdateTable {
      type Table = T 
      val  table = t: t.type

      val input = (newReadThroughput, newWriteThroughput)

      val inputState = inputSt
    }
  }
