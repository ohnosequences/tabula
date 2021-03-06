// package ohnosequences.tabula.impl

// import ohnosequences.tabula._, ImplicitConversions._
// import com.amazonaws.services.dynamodbv2.model._

// case class DeleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A)
//   (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

//   type OutC[X] = X

//   def apply(): Out = {
//     import scala.collection.JavaConversions._
//     println("executing: " + action)

//     val res: ohnosequences.tabula.DeleteItemResult = try {
//       dynamoClient.client.deleteItem(action.table.name, Map(
//         action.table.hashKey.label -> getAttrVal(action.input)
//       ))
//       DeleteItemSuccess
//     } catch {
//       case t: Exception => println(t.printStackTrace); DeleteItemFail
//     }

//     ExecutorResult(res, action.table, inputState)
//   }
// }

// case class DeleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A)
//   (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

//   type OutC[X] = X

//   def apply(): Out = {
//     import scala.collection.JavaConversions._
//     println("executing: " + action)

//     val res: ohnosequences.tabula.DeleteItemResult = try {
//       dynamoClient.client.deleteItem(action.table.name, Map(
//         action.table.hashKey.label -> getAttrVal(action.input._1),
//         action.table.rangeKey.label -> getAttrVal(action.input._2)
//       ))
//       DeleteItemSuccess
//     } catch {
//       case t: Exception => println(t.printStackTrace); DeleteItemFail
//     }

//     ExecutorResult(res, action.table, inputState)
//   }
// }
