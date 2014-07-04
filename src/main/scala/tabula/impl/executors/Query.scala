package ohnosequences.tabula.impl

import ohnosequences.tabula._, Condition._, AnyPredicate._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._


case class QueryExecutor[A <: AnyQueryAction with SDKRepParser](a: A)
  (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
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

    ExecutorResult(res, action.table, action.inputState)
  }
}
