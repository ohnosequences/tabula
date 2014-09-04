package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._, AnyFn._, AnyTaggedType.Tagged
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, Condition._, AnyPredicate._, ImplicitConversions._, AnyAction._, AnyTableItemAction._
import com.amazonaws.services.dynamodbv2.model._

// trait AnyQueryExecutorResult extends AnyExecutorResult { 
//   type Action <: AnyQueryAction
//   type Output <: OutputOf[Action]
//   type OutputState <: OutputStateOf[Action]
// }

trait AnyQueryExecutor {
  type Action <: AnyQueryAction
}

case class QueryExecutor[A <: AnyQueryAction](a: A)(
  dynamoClient: AnyDynamoDBClient,
  parser: (PropertiesOf[ItemOf[A]] ParseFrom SDKRep) with out[RawOf[ItemOf[A]]]
) extends Executor[A](a) with AnyQueryExecutor {

  type OutC[X] = X

  type Action = A
  import scala.collection.JavaConversions._

  def apply(inputState: InputStateOf[A]): Out = {
    println("executing: " + action)

    val res: List[Tagged[ItemOf[A]]] = try {
      // val predicate = SimplePredicate(action.item, EQ(action.table.hashKey, action.input))
      // val (_, keyConditions) = toSDKPredicate(predicate)
      val (_, keyConditions) = toSDKPredicate(action.input)

      val queryRequest = new QueryRequest()
        .withTableName(action.table.name)
        .withKeyConditions(keyConditions)

      val SDKRepsList: List[Map[String, AttributeValue]] = 
        dynamoClient.client.query(queryRequest)
          .getItems.map(_.toMap).toList

      SDKRepsList.map{ rep => (action.item: ItemOf[A]) =>> parser(action.item.properties, rep) }
    } catch {
      case t: Exception => List()
    }
    val uh: A#OutputState = inputState
    ExecutorResult[A, A#Output, A#OutputState](action, res, uh)
    // (res, inputState)
  }
}
