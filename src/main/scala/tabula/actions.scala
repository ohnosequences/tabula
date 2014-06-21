package ohnosequences.tabula

import ohnosequences.scarph._


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
  type Table <: Singleton with AnyTable
  val  table: Table

  // TODO: change this to ResourcesList
  type Resources = Table //:+: RNil
  val  resources = table
}

object AnyTableAction {
  type withHashKeyTable      = AnyTableAction { type Table <: Singleton with AnyHashKeyTable }
  type withCompositeKeyTable = AnyTableAction { type Table <: Singleton with AnyCompositeKeyTable }
}
