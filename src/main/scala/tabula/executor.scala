package ohnosequences.tabula

trait AnyExecutor {
  type Action <: AnyAction
  val  action: Action

  // class ExecutorOut(val output: action.Output, val )

  type OutC[+X]
  type Out = OutC[(action.Output, action.Resources, action.OutputState)]

  def apply(): Out
}

abstract class Executor[A <: AnyAction](val action: A) 
  extends AnyExecutor { type Action = A }

object Executor {
  type Aux[A <: AnyAction, C[+_]] = AnyExecutor { type Action = A; type OutC[+X] = C[X] }
  type Id[A <: AnyAction] = AnyExecutor { type Action = A; type OutC[+X] = X }
  type For[A <: AnyAction] = AnyExecutor { type Action = A }
  type inRegion[R <: AnyRegion] = AnyExecutor { type Action <: AnyAction.inRegion[R] }
}
