package ohnosequences.tabula

case object actions {

  import items._, regions._, resources._, tables._


  trait AnyAction {

    type InputState
    type OutputState

    // // these are input and output that are not resources
    // type Input
    // val  input: Input

    type Output
  }

  object AnyAction {
    // TODO: this won't work with ResourcesList
    type inRegion[R <: AnyRegion] = AnyAction { type Resources <: AnyDynamoDBResource.inRegion[R] }
  }


  trait AnyTableAction extends AnyAction {

    type Table <: AnyTable
    val  table: Table
  }

  abstract class TableAction[T <: AnyTable](val table: T)
    extends AnyTableAction { type Table = T }


  trait AnyItemAction extends AnyAction {
    type Item <: AnyItem
    val  item: Item

    type Table = Item#Table
    lazy val table: Table = item.table
  }


  abstract class TableItemAction[I <: AnyItem](val item: I)
    extends AnyItemAction { type Item = I }

  object AnyTableAction {
    type withHashKeyTable      = AnyTableAction { type Table <: AnyTable.withHashKey }
    type withCompositeKeyTable = AnyTableAction { type Table <: AnyTable.withCompositeKey }
  }

}
