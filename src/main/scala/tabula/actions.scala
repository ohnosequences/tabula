package ohnosequences.tabula

trait AnyAction { action =>
  // this should be an HList of Resources; it is hard to express though
  type Resources
  val  resources: Resources

  type InputState
  type OutputState

  // these are input and output that are not resources
  type Input
  val  input: Input

  type Output
}

object AnyAction {
  // TODO: this won't work with ResourcesList
  type inRegion[R <: AnyRegion] = AnyAction { type Resources <: AnyDynamoDBResource.inRegion[R] }

  type InputOf[A <: AnyAction] = A#Input
  type OutputOf[A <: AnyAction] = A#Output
  type OutputStateOf[A <: AnyAction] = A#OutputState
  type InputStateOf[A <: AnyAction] = A#InputState
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
  type Item <: AnyItem
  val  item: Item

  type Table <: AnyItem.TableOf[Item]
  // val  table = item.table
}

object AnyTableItemAction {
  type ItemOf[A <: AnyTableItemAction] = A#Item
}

abstract class TableItemAction[I <: AnyItem](val item: I)
  extends AnyTableItemAction { type Item = I }

object AnyTableAction {
  type withHashKeyTable      = AnyTableAction { type Table <: AnyTable.withHashKey }
  type withCompositeKeyTable = AnyTableAction { type Table <: AnyTable.withCompositeKey }
}
