package ohnosequences.tabula

case object actions {

  import items._, regions._, states._, resources._, tables._


  trait AnyAction {

    type InputState <: AnyDynamoDBState
    type OutputState <: AnyDynamoDBState

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

  // We compose actions cheking that the transition state is compatible
  trait AnyChainedAction extends AnyAction {

    type First <: AnyAction
    val  first: First

    type Second <: AnyAction { type InputState <: First#OutputState }
    val  second: Second

    type InputState = First#InputState
    type OutputState = Second#OutputState

    type Output = Second#Output
  }

  case class Chain[
    F <: AnyAction,
    S <: AnyAction { type InputState <: F#OutputState }
  ](val first: F, val second: S) extends AnyChainedAction {

    type First = F
    type Second = S
  }

  type >>[F <: AnyAction, S <: AnyAction { type InputState <: F#OutputState }] = Chain[F, S]

  implicit def actionOps[F <: AnyAction](f: F):
        ActionOps[F] =
    new ActionOps[F](f)

  case class ActionOps[F <: AnyAction](val f: F) extends AnyVal {

    def >>[S <: AnyAction { type InputState <: F#OutputState }](s: S): F >> S = Chain[F, S](f, s)
  }

}
