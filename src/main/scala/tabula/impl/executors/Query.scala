package ohnosequences.tabula.impl

import ohnosequences.tabula._, Condition._, AnyPredicate._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._


case class QueryExecutor[Action <: AnyQueryAction with SDKRepParser]
  (dynamoClient: AnyDynamoDBClient) extends Executor[Action] {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(action: Action)(inputState: action.InputState): OutC[ExecutorResult[action.Output, action.OutputState]] = {
    println("executing: " + action)

    val res = try {
      // val predicate = SimplePredicate(action.item, EQ(action.table.hashKey, action.input))
      // val (_, keyConditions) = toSDKPredicate(predicate)
      val (_, keyConditions) = toSDKPredicate(action.input)

      val queryRequest = new QueryRequest()
        .withTableName(action.table.name)
        .withKeyConditions(keyConditions)

      val toSDKRep: List[Map[String, AttributeValue]] = 
        dynamoClient.client.query(queryRequest)
          .getItems.map(_.toMap).toList

      QuerySuccess[action.Item](toSDKRep.map(action.parseSDKRep))
    } catch {
      case t: Exception => QueryFailure[action.Item](t.toString)
    }

    ExecutorResult(res, inputState)
  }
}
