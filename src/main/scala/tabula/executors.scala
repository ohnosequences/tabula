package ohnosequences.tabula

import AnyAction._
trait AnyExecutorResult { 
  type Action <: AnyAction
  // type Output <: OutputOf[Action]
  // type OutputState <: OutputStateOf[Action]
}
case class ExecutorResult[A <: AnyAction, O <: A#Output, S <: A#OutputState](a: A, output: O, state: S)
  extends AnyExecutorResult { 
    type Action = A 
    // type Output = O
    // type OutputState = OS
  }

trait AnyExecutor {
  
  import Executor._

  type Action <: AnyAction
  val  action: Action

  type OutC[X]
  // type Out = OutC[ExecutorResult[ActionOf[Me]]]
  type Out = OutC[AnyExecutorResult]
  // type Out = (OutC[ActionOf[Me]#Output], ActionOf[Me]#OutputState)

  def apply(inputState: InputStateOf[Action]): Out
}

abstract class Executor[A <: AnyAction](val action: A) extends AnyExecutor { type Action = A }

object Executor {
  type Aux[A <: AnyAction, C[_]] = AnyExecutor { type Action = A; type OutC[X] = C[X] }
  type Id[A <: AnyAction] = AnyExecutor { type Action = A; type OutC[X] = X }
  type For[A <: AnyAction] = AnyExecutor { type Action = A }
  type inRegion[R <: AnyRegion] = AnyExecutor { type Action <: AnyAction.inRegion[R] }

  type OutOf[E <: AnyExecutor] = E#Out
  type ActionOf[E <: AnyExecutor] = E#Action

}
