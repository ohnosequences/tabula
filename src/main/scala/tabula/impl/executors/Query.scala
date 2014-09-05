package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._, AnyFn._, AnyTaggedType.Tagged
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, Condition._, AnyPredicate._, ImplicitConversions._, AnyAction._, AnyItemAction._
import com.amazonaws.services.dynamodbv2.model._

case class QueryExecutor[I <: AnyItem.OfCompositeTable, A <: QueryActionFor[I]](
  dynamoClient: AnyDynamoDBClient,
  parser: (PropertiesOf[I] ParseFrom SDKRep) with out[RawOf[I]]
) extends ExecutorFor[A] {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(action: A)(inputState: InputStateOf[A]): Out = {
    println("executing: " + action)

    val res: List[Tagged[I]] = try {
      // val predicate = SimplePredicate(action.item, EQ(action.table.hashKey, action.input))
      // val (_, keyConditions) = toSDKPredicate(predicate)
      val (_, keyConditions) = toSDKPredicate(action.predicate)

      val queryRequest = new QueryRequest()
        .withTableName(action.item.table.name)
        .withKeyConditions(keyConditions)

      val SDKRepsList: List[Map[String, AttributeValue]] = 
        dynamoClient.client.query(queryRequest)
          .getItems.map(_.toMap).toList

      SDKRepsList.map{ rep => (action.item: I) =>> parser(action.item.properties, rep) }
    } catch {
      // FIXME: error handling
      case t: Exception => List()
    }

    ExecutorResult[A](res, inputState)
  }
}
