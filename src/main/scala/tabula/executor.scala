package ohnosequences.tabula

trait Executor {
  type Action <: AnyAction

  type C[+X]
  type Out = C[(Action#Output, Action#OutputState)]

  def apply(action: Action): Out
}

object Executor {
  type For[A <: AnyAction] = Executor { type Action = A }
  type inRegion[R <: AnyRegion] = Executor { type Action <: AnyAction.inRegion[R] }
}

