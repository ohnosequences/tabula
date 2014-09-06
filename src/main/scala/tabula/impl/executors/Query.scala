package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._, AnyFn._, AnyType._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, Condition._, AnyPredicate._, ImplicitConversions._, AnyAction._, AnyItemAction._
import com.amazonaws.services.dynamodbv2.model._

case class QueryExecutor[A <: AnyQuery](
  dynamoClient: AnyDynamoDBClient,
  parser: (A#Item#Properties ParseFrom SDKRep) with out[A#Item#Raw]
  // parser: (PropertiesOf[ItemOf[A]] ParseFrom SDKRep) with out[RawOf[ItemOf[A]]]
) extends ExecutorFor[A] {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(action: A)(inputState: InputStateOf[A]): Out = {
    println("executing: " + action)

    val res: List[ValueOf[ItemOf[A]]] = try {
      // val predicate = SimplePredicate(action.item, EQ(action.table.hashKey, action.input))
      // val (_, keyConditions) = toSDKPredicate(predicate)
      val (_, keyConditions) = toSDKPredicate(action.predicate)

      val queryRequest = new QueryRequest()
        .withTableName(action.item.table.name)
        .withKeyConditions(keyConditions)

      val SDKRepsList: List[Map[String, AttributeValue]] = 
        dynamoClient.client.query(queryRequest)
          .getItems.map(_.toMap).toList

      SDKRepsList.map{ rep => valueOf[ItemOf[A]](parser(action.item.properties, rep)) }
    } catch {
      // FIXME: error handling
      case t: Exception => List()
    }

    ExecutorResult[A](res, inputState)
  }
}
