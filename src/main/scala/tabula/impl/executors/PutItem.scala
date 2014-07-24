package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

case class PutItemExecutor[Action <: AnyPutItemAction with SDKRepGetter]
  (dynamoClient: AnyDynamoDBClient) extends Executor[Action] {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(action: Action)(inputState: action.InputState): OutC[ExecutorResult[action.Output, action.OutputState]] = {
    println("executing: " + action)

    val res: ohnosequences.tabula.PutItemResult = try {
      dynamoClient.client.putItem(action.table.name, action.getSDKRep(action.input)); PutItemSuccess
    } catch {
      case t: Exception => PutItemFail(t.toString)
    }

    ExecutorResult(res, inputState)
  }
}
