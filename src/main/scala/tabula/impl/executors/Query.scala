package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, fns._, types._
import ohnosequences.cosas.ops.typeSets._

import ohnosequences.tabula._, actions._, conditions._, predicates._, executors._
import ImplicitConversions._

import com.amazonaws.services.dynamodbv2.model._

case class QueryExecutor[A <: action.AnyQuery](implicit
  dynamoClient: AnyDynamoDBClient,
  parser: (A#Item#Properties ParseFrom SDKRep) { type Out = A#Item#Raw }
) extends ExecutorFor[A] {

  type OutC[X] = X


  import scala.collection.JavaConversions._

  def apply(action: Action)(inputState: Action#InputState): Out = {
    println("executing: " + action)

    // : List[ValueOf[Action#Item]]
    val res = try {
      // val predicate = SimplePredicate(action.item, EQ(action.table.hashKey, action.input))
      // val (_, keyConditions) = toSDKPredicate(predicate)
      val (_, keyConditions) = toSDKPredicate(action.predicate)

      val queryRequest = new QueryRequest()
        .withTableName(action.item.table.name)
        .withKeyConditions(keyConditions)

      val SDKRepsList: List[Map[String, AttributeValue]] =
        dynamoClient.client.query(queryRequest)
          .getItems.map(_.toMap).toList

      // TODO: Check this valueOf
      // SDKRepsList.map{ rep => valueOf[Action#Item, Action#Item#Raw](action.item)(parser(action.item.properties:Action#Item#Properties, rep)) }
      SDKRepsList.map{ rep => new Denotes[Action#Item#Raw, Action#Item](parser(action.item.properties:Action#Item#Properties, rep)) }
      // SDKRepsList.map{ rep => (action.item := parser(action.item.properties:Action#Item#Properties, rep)) }
    } catch {
      // FIXME: error handling
      case t: Exception => throw t
    }

    // FIXME: ???
    ???
    // ExecutorResult[Action](res, inputState)
  }
}
