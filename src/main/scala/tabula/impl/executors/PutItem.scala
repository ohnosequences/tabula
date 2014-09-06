package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._, AnyType._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, ImplicitConversions._, AnyAction._, AnyItemAction._
import com.amazonaws.services.dynamodbv2.model._

case class PutItemExecutor[A <: AnyPutItem](
  dynamoClient: AnyDynamoDBClient, 
  serializer: RawOf[ItemOf[A]] SerializeTo SDKRep
) extends ExecutorFor[A] {

  type OutC[X] = X

  import scala.collection.JavaConversions._

  def apply(action: A)(inputState: InputStateOf[A]): Out = {
    println("executing: " + action)

    try {
      dynamoClient.client.putItem(action.item.table.name, serializer(action.itemValue.raw))
    } catch {
      // FIXME: error handling
      case t: Exception => None
    }

    ExecutorResult[A](None, inputState)
  }
}
