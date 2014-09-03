package ohnosequences.tabula

trait AnyAction { action =>
  // this should be an HList of Resources; it is hard to express though
  type Resources
  val  resources: Resources

  type InputState
  val  inputState: InputState

  type OutputState

  // these are input and output that are not resources
  type Input
  val  input: Input

  type Output
}

object AnyAction {
  // TODO: this won't work with ResourcesList
  type inRegion[R <: AnyRegion] = AnyAction { type Resources <: AnyDynamoDBResource.inRegion[R] }
}


trait AnyTableAction extends AnyAction {
  
  type Table <: AnyTable
  val  table: Table

  // TODO: change this to ResourcesList
  type Resources = Table //:+: RNil
  val  resources = table
}

abstract class TableAction[T <: AnyTable](val table: T)
  extends AnyTableAction { type Table = T }


trait AnyTableItemAction extends AnyTableAction {
  type Item <: AnyItem //.ofTable[Table]
  val  item: Item
}

abstract class TableItemAction[
  T <: AnyTable, 
  I <: AnyItem.ofTable[T]
](val table: T, val item: I)
extends AnyTableItemAction { 
  type Table = T
  type Item = I 
}


object AnyTableAction {
  type withHashKeyTable      = AnyTableAction { type Table <: AnyHashKeyTable }
  type withCompositeKeyTable = AnyTableAction { type Table <: AnyCompositeKeyTable }
}
