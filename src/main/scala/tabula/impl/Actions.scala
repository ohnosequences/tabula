package ohnosequences.tabula.impl

import ohnosequences.tabula._
import ohnosequences.tabula.InitialState

trait DeleteTableAux extends AnyAction {
  override type Input <: AnyTable with Singleton
  override val input: Input //table
  override type Output = Input
  override type InputState = Active[Input]
  override type OutputState = Deleting[Output]
}

class DeleteTable[T <: AnyTable with Singleton]  ( table: T,  val state:  Active[T]) extends DeleteTableAux {
  override val input: Input = table
  override type Input = T
}

object DeleteTable {

  implicit class DeleteTableExecute[D <: DeleteTableAux](ac: D)(implicit dynamoClient: DynamoDBClient) extends Execute {

    override type Action = D
    override val action = ac


    override def apply(): (action.Input, Deleting[action.Input]) = {
      println("executing: " + action)
//      action.state match {
//        case c: Creating[_] => c.deleting
//      }

     // val active: Active[action.Input] = action.state
     // val deleting: Deleting[action.Input] = action.state.deleting
      (action.input, action.state.deleting)
    }

    override type C[+X] = X

  }
}


//trait CreateTableAux extends AnyDeleteTable {
//  override type Input <: AnyTable with Singleton
//  val state: InitialState[Input]
//  override val input: Input //table
//  override type InputState = InitialState[Input]
//  override type OutputState = Deleting[Input]
//
//
//}
//
//class DeleteTable[T <: AnyTable with Singleton]  ( table: T,  val state: InitialState[T]) extends DeleteTableAux {
//  override val input: Input = table
//  override type Input = T
//}
//
//object DeleteTable {
//
//  implicit class DeleteTableExecute[D <: DeleteTableAux](ac: D)(implicit dynamoClient: DynamoDBClient) extends Execute {
//
//    override type Action = D
//    override val action = ac
//
//
//    override def apply(): (action.Input, action.OutputState) = {
//      println("executing: " + action)
//      (action.input, action.state.deleting)
//    }
//
//    override type C[+X] = X
//
//  }
//}
