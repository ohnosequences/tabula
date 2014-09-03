package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

case class PutItemExecutor[Action <: AnyPutItemAction](a: Action)(
  dynamoClient: AnyDynamoDBClient, 
  serializer: RawOf[Action#Item] SerializeTo SDKRep
) extends Executor[Action](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._

  def apply(inputState: Action#InputState): Out = {
    println("executing: " + action)

    val res: ohnosequences.tabula.PutItemResult = try {
      dynamoClient.client.putItem(action.table.name, serializer(action.input)); PutItemSuccess
    } catch {
      case t: Exception => PutItemFail(t.toString)
    }

    ExecutorResult[Action](res, inputState)
  }
}
