package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

case class QueryExecutor[A <: AnyQueryAction with SDKRepParser](a: A)
  (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
    println("executing: " + action)

    val res = try {
      val (_, keyConditions) = toSDKPredicate(action.keyConditions)

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
