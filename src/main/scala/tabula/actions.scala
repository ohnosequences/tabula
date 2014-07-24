package ohnosequences.tabula

import ohnosequences.scarph._


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
}


trait AnyTableAction extends AnyAction {
  type Table <: AnyTable
  val  table: Table

  // TODO: change this to ResourcesList
  type Resources = Table //:+: RNil
  val  resources = table
}

abstract class TableAction[T <: Singleton with AnyTable](val table: T)
  extends AnyTableAction { type Table = T }


trait AnyTableItemAction extends AnyTableAction {
  type Item <: Singleton with AnyItem
  val  item: Item

  type Table = item.Table
  val  table = item.table
}

abstract class TableItemAction[I <: Singleton with AnyItem](val item: I)
  extends AnyTableItemAction { type Item = I }


object AnyTableAction {
  type withHashKeyTable      = AnyTableAction { type Table <: Singleton with AnyTable.withHashKey }
  type withCompositeKeyTable = AnyTableAction { type Table <: Singleton with AnyTable.withCompositeKey }
}
