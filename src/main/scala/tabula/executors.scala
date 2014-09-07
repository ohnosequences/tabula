package ohnosequences.tabula

import AnyAction._

case class ExecutorResult[A <: AnyAction](output: A#Output, state: A#OutputState)
// case class ExecutorResult[O, S](output: O, state: S)

trait AnyExecutor {
  
  import Executor._

  type Action <: AnyAction

  type OutC[X]
  type Out <: OutC[ExecutorResult[Action]]

  def apply(action: Action)(inputState: InputStateOf[Action]): Out
}

// abstract class Executor[A <: AnyAction](val action: A) extends AnyExecutor { type Action = A }

trait ExecutorFor[A <: AnyAction] extends AnyExecutor {
  
  type Action = A
  type Out = OutC[ExecutorResult[A]]
}

trait TableExecutorFor[T <: AnyTable, A <: AnyTableAction] extends AnyExecutor {
  type Action = A
  type Out = OutC[ExecutorResult[A]]
}

object Executor {
  type Aux[A <: AnyAction, C[_]] = AnyExecutor { type Action = A; type OutC[X] = C[X] }
  type Id[A <: AnyAction] = AnyExecutor { type Action = A; type OutC[X] = X }
  type For[A <: AnyAction] = AnyExecutor { type Action = A }
  type inRegion[R <: AnyRegion] = AnyExecutor { type Action <: AnyAction.inRegion[R] }

  type OutOf[E <: AnyExecutor] = E#Out
  type ActionOf[E <: AnyExecutor] = E#Action
}
