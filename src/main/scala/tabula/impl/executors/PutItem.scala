package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

case class PutItemExecutor[A <: AnyPutItemAction](a: A)(
    dynamoClient: AnyDynamoDBClient
  ) extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
    println("executing: " + action)

    val res: ohnosequences.tabula.PutItemResult = try {
      dynamoClient.client.putItem(action.table.name, action.getSDKRep(action.input)); PutItemSuccess
    } catch {
      case t: Throwable => println(t.printStackTrace); PutItemFail
    }
    ExecutorResult(res, action.table, action.inputState)
  }
}
