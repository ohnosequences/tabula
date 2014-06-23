package ohnosequences.tabula.impl

import ohnosequences.tabula._, AttributeImplicits._
import com.amazonaws.services.dynamodbv2.model._

case class DeleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A)
  (dynamoClient: AnyDynamoDBClient) 
    extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
    println("executing: " + action)

    val res: ohnosequences.tabula.DeleteItemResult = try {
      dynamoClient.client.deleteItem(action.table.name, Map(
        action.table.hashKey.label -> getAttrVal(action.input)
      ))
      DeleteItemSuccess
    } catch {
      case t: Throwable => println(t.printStackTrace); DeleteItemFail
    }

    ExecutorResult(res, action.table, action.inputState)
  }
}

case class DeleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A)
  (dynamoClient: AnyDynamoDBClient) 
    extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
    println("executing: " + action)

    val res: ohnosequences.tabula.DeleteItemResult = try {
      dynamoClient.client.deleteItem(action.table.name, Map(
        action.table.hashKey.label -> getAttrVal(action.input._1),
        action.table.rangeKey.label -> getAttrVal(action.input._2)
      ))
      DeleteItemSuccess
    } catch {
      case t: Throwable => println(t.printStackTrace); DeleteItemFail
    }

    ExecutorResult(res, action.table, action.inputState)
  }
}
