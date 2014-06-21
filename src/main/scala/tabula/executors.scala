package ohnosequences.tabula

case class ExecutorResult[O, R, OS](output: O, resources: R, state: OS)

trait AnyExecutor {
  type Action <: AnyAction
  val  action: Action

  type OutC[+X]
  type Out = OutC[ExecutorResult[action.Output, action.Resources, action.OutputState]]

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
