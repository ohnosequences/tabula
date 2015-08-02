package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, types._
import ohnosequences.cosas.ops.typeSets._

import ohnosequences.tabula._, actions._, executors._, items._
import ImplicitConversions._

import com.amazonaws.services.dynamodbv2.model._


case class PutItemExecutor[I <: AnyItem](
  dynamoClient: AnyDynamoDBClient,
  serializer: I#Raw SerializeTo SDKRep
) extends ExecutorFor[action.PutItem[I]] {

  type OutC[X] = X

  def apply(action: Action)(inputState: Action#InputState): Out = {

    import scala.collection.JavaConversions._
    println("executing: " + action)

    try {
      val serializedItem: SDKRep = serializer(action.itemValue.value)
      dynamoClient.client.putItem(action.item.table.name, serializedItem)
    } catch {
      // FIXME: error handling
      case t: Exception => None
    }

    ExecutorResult[Action](None, inputState)
  }
}
