package ohnosequences.tabula

case class ExecutorResult[O, S](output: O, state: S)

trait AnyExecutor {
  type Action <: AnyAction
  // val  action: Action

  type OutC[X]
  // type Out = 

  def apply(action: Action)(inputState: action.InputState): OutC[ExecutorResult[action.Output, action.OutputState]]
}

abstract class Executor[A <: AnyAction] extends AnyExecutor { type Action = A }

object Executor {
  type Aux[A <: AnyAction, C[_]] = AnyExecutor { type Action = A; type OutC[X] = C[X] }
  type Id[A <: AnyAction] = AnyExecutor { type Action = A; type OutC[X] = X }
  type For[A <: AnyAction] = AnyExecutor { type Action = A }
  type inRegion[R <: AnyRegion] = AnyExecutor { type Action <: AnyAction.inRegion[R] }
}
