package ohnosequences.tabula

trait Executor {
  type Action <: AnyAction

  type C[+X]
  type Out = C[(Action#Output, Action#Resources, Action#OutputState)]

  def apply(action: Action): Out
}

object Executor {
  type For[A <: AnyAction] = Executor { type Action = A }
  type inRegion[R <: AnyRegion] = Executor { type Action <: AnyAction.inRegion[R] }
}

trait ExecutorFrom[A <: AnyAction] {
  type Exec <: Executor.For[A]
  type Out = Exec#Out
  def apply(a: A): Exec
}

object ExecutorFrom {
  type Aux[A <: AnyAction, E <: Executor.For[A]] = ExecutorFrom[A] { type Exec = E }
}
