package ohnosequences.tabula

trait AnyDeleteItemHashKey extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = Table#HashKey#Raw
  val  hashKeyValue: Input
  val  input = hashKeyValue

  type Output = None.type
}

case class DeleteItemHashKey[
    T <: AnyHashKeyTable with Singleton, 
    H <: T#HashKey#Raw
  ](table: T, 
    inputState: AnyTableState.For[T] with ReadyTable, 
    hashKeyValue: H
  ) extends AnyDeleteItemHashKey { type Table = T }

trait AnyDeleteItemCompositeKey extends AnyTableAction {
  type Table <: Singleton with AnyCompositeKeyTable

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = (Table#HashKey#Raw, Table#RangeKey#Raw)
  val hashKeyValue: Table#HashKey#Raw
  val rangeKeyValue: Table#RangeKey#Raw
  val  input = (hashKeyValue, rangeKeyValue)

  type Output = None.type
}

// TODO
case class DeleteItemCompositeKey[
    T <: AnyCompositeKeyTable with Singleton, 
    RH <: T#HashKey#Raw, 
    RR <: T#RangeKey#Raw
  ](table: T, 
    inputState: AnyTableState.For[T] with ReadyTable, 
    hashKeyValue: RH, 
    rangeKeyValue: RR
  ) extends AnyDeleteItemCompositeKey { type Table = T }
