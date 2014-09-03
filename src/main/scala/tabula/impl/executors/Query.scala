// package ohnosequences.tabula.impl

// import ohnosequences.pointless._, AnyRecord._, AnyFn._
// import ohnosequences.pointless.ops.typeSet._
// import ohnosequences.tabula._, Condition._, AnyPredicate._, ImplicitConversions._
// import com.amazonaws.services.dynamodbv2.model._

// case class QueryExecutor[Action <: AnyQueryAction](a: Action)(
//   dynamoClient: AnyDynamoDBClient,
//   parser: (PropertiesOf[Action#Item] ParseFrom SDKRep) with out[RawOf[Action#Item]]
// ) extends Executor[Action](a) {

//   type OutC[X] = X

//   import scala.collection.JavaConversions._

//   def apply(inputState: Action#InputState): Out = {
//     println("executing: " + action)

//     val res = try {
//       // val predicate = SimplePredicate(action.item, EQ(action.table.hashKey, action.input))
//       // val (_, keyConditions) = toSDKPredicate(predicate)
//       val (_, keyConditions) = toSDKPredicate(action.input)

//       val queryRequest = new QueryRequest()
//         .withTableName(action.table.name)
//         .withKeyConditions(keyConditions)

//       val SDKRepsList: List[Map[String, AttributeValue]] = 
//         dynamoClient.client.query(queryRequest)
//           .getItems.map(_.toMap).toList

//       SDKRepsList.map{ rep => (action.item: Action#Item) =>> parser(action.item.properties, rep) }
//     } catch {
//       case t: Exception => List() //QueryFailure[action.Item](t.toString)
//     }

//     ExecutorResult[Action](res, inputState)
//   }
// }
