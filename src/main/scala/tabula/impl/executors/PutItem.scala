package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, types._
import ohnosequences.cosas.ops.typeSets._
import ohnosequences.tabula._, ImplicitConversions._, AnyAction._, AnyItemAction._
import com.amazonaws.services.dynamodbv2.model._

case class PutItemExecutor[A <: AnyPutItem](
  dynamoClient: AnyDynamoDBClient,
  serializer: ItemOf[A]#Raw SerializeTo SDKRep
) extends ExecutorFor[A] {

  type OutC[X] = X

  import scala.collection.JavaConversions._

  def apply(action: A)(inputState: InputStateOf[A]): Out = {
    println("executing: " + action)

    try {
      dynamoClient.client.putItem(action.item.table.name, serializer(action.itemValue.value))
    } catch {
      // FIXME: error handling
      case t: Exception => None
    }

    ExecutorResult[A](None, inputState)
  }
}
