package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, ImplicitConversions._, AnyAction._
import com.amazonaws.services.dynamodbv2.model._

case class PutItemExecutor[I <: AnyItem, A <: PutItem[I]](
  dynamoClient: AnyDynamoDBClient, 
  serializer: RawOf[A#Item] SerializeTo SDKRep
) extends ExecutorFor[A] {

  type OutC[X] = X

  import scala.collection.JavaConversions._

  def apply(action: A)(inputState: InputStateOf[A]): Out = {
    println("executing: " + action)

    try {
      dynamoClient.client.putItem(action.item.table.name, serializer(action.itemValue))
    } catch {
      // FIXME: error handling
      case t: Exception => None
    }

    ExecutorResult[A](None, inputState)
  }
}
