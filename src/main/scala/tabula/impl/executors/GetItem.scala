package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._
import ohnosequences.typesets.AnyTag._

case class GetItemHashKeyExecutor [
  A <: AnyGetItemHashKeyAction with SDKRepParser
](
  val a: A
)(
 val dynamoClient: AnyDynamoDBClient
) extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._

  def apply(): Out = {

    println("executing: " + action)

    val res: ohnosequences.tabula.GetItemResult[action.Item] = try {

      val getItemRequest = new GetItemRequest()
        .withTableName(action.table.name)
        .withKey(Map(
          action.table.hashKey.label -> getAttrVal(action.input)
        ))

      val toSDKRep = dynamoClient.client.getItem(getItemRequest).getItem

      GetItemSuccess(action.parseSDKRep(toSDKRep.toMap))
    } catch {
      case t: Exception => GetItemFailure(t.toString)
    }

    ExecutorResult(res, action.table, action.inputState)
  }
}

case class GetItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction with SDKRepParser](a: A)
  (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
    println("executing: " + action)

    val res: ohnosequences.tabula.GetItemResult[action.Item] = try {

      val getItemRequest = new GetItemRequest()
        .withTableName(action.table.name)
        .withKey(Map(
          action.table.hashKey.label -> getAttrVal(action.input._1),
          action.table.rangeKey.label -> getAttrVal(action.input._2)
        ))

      val toSDKRep = dynamoClient.client.getItem(getItemRequest).getItem

      GetItemSuccess(action.parseSDKRep(toSDKRep.toMap))
    } catch {
      case t: Exception => GetItemFailure(t.toString)
    }

    ExecutorResult(res, action.table, action.inputState)
  }
}
