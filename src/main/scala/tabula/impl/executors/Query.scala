package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, fns._, types._
import ohnosequences.cosas.ops.typeSets._

import ohnosequences.tabula._, actions._, conditions._, predicates._, executors._
import ImplicitConversions._

import com.amazonaws.services.dynamodbv2.model._

case class QueryExecutor[A <: action.AnyQuery](implicit
  dynamoClient: AnyDynamoDBClient,
  parser: (A#Item#Properties ParseFrom SDKRep) { type Out = A#Item#Raw }
  // parser: (PropertiesOf[ItemOf[A]] ParseFrom SDKRep) with out[RawOf[ItemOf[A]]]
) extends ExecutorFor[A] {

  type OutC[X] = X


  import scala.collection.JavaConversions._

  def apply(action: A)(inputState: A#InputState): Out = {
    println("executing: " + action)

    val res: List[ValueOf[A#Item]] = try {
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
      SDKRepsList.map{ rep => valueOf[A#Item, A#Item#Raw](action.item)(parser(action.item.properties:A#Item#Properties, rep)) }
    } catch {
      // FIXME: error handling
      case t: Exception => throw t
    }

    // FIXME: ???
    // ExecutorResult[A](res, inputState)
    ???
  }
}
