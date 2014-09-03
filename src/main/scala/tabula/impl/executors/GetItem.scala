// // package ohnosequences.tabula.impl

// <<<<<<< HEAD
// import ohnosequences.tabula._, ImplicitConversions._
// import com.amazonaws.services.dynamodbv2.model._
// import ohnosequences.pointless.AnyTaggedType._

// case class GetItemHashKeyExecutor [
//   A <: AnyGetItemHashKeyAction with SDKRepParser
// ](
//   val a: A
// )(
//  val dynamoClient: AnyDynamoDBClient
// ) extends Executor[A](a) {
// =======
// // import ohnosequences.tabula._, ImplicitConversions._
// // import com.amazonaws.services.dynamodbv2.model._

// // case class GetItemHashKeyExecutor[A <: AnyGetItemHashKeyAction with SDKRepParser](a: A)
// //   (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {
// >>>>>>> feature/table/ops

// //   type OutC[X] = X

// <<<<<<< HEAD
//   import scala.collection.JavaConversions._

//   def apply(): Out = {

//     println("executing: " + action)

//     val res: ohnosequences.tabula.GetItemResult[action.Item] = try {
// =======
// //   import scala.collection.JavaConversions._
// //   def apply(): Out = {
// //     println("executing: " + action)

// //     val res = try {
// >>>>>>> feature/table/ops

// //       val getItemRequest = new GetItemRequest()
// //         .withTableName(action.table.name)
// //         .withKey(Map(
// //           action.table.hashKey.label -> getAttrVal(action.input)
// //         ))

// //       val toSDKRep = dynamoClient.client.getItem(getItemRequest).getItem

// <<<<<<< HEAD
//       GetItemSuccess(action.parseSDKRep(toSDKRep.toMap))
//     } catch {
//       case t: Exception => GetItemFailure(t.toString)
//     }
// =======
// //       GetItemSuccess(action.parseSDKRep(toSDKRep.toMap))
// //     } catch {
// //       case t: Exception => GetItemFailure[action.Item](t.toString)
// //     }
// >>>>>>> feature/table/ops

// //     ExecutorResult(res, action.table, inputState)
// //   }
// // }

// // case class GetItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction with SDKRepParser](a: A)
// //   (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

// //   type OutC[X] = X

// //   import scala.collection.JavaConversions._
// //   def apply(): Out = {
// //     println("executing: " + action)

// <<<<<<< HEAD
//     val res: ohnosequences.tabula.GetItemResult[action.Item] = try {
// =======
// //     val res = try {
// >>>>>>> feature/table/ops

// //       val getItemRequest = new GetItemRequest()
// //         .withTableName(action.table.name)
// //         .withKey(Map(
// //           action.table.hashKey.label -> getAttrVal(action.input._1),
// //           action.table.rangeKey.label -> getAttrVal(action.input._2)
// //         ))

// //       val toSDKRep = dynamoClient.client.getItem(getItemRequest).getItem

// <<<<<<< HEAD
//       GetItemSuccess(action.parseSDKRep(toSDKRep.toMap))
//     } catch {
//       case t: Exception => GetItemFailure(t.toString)
//     }
// =======
// //       GetItemSuccess(action.parseSDKRep(toSDKRep.toMap))
// //     } catch {
// //       case t: Exception => GetItemFailure[action.Item](t.toString)
// //     }
// >>>>>>> feature/table/ops

// //     ExecutorResult(res, action.table, inputState)
// //   }
// // }
