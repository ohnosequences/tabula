package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, types._
import ohnosequences.cosas.ops.typeSets._

import ohnosequences.tabula._, actions._, executors._
import ImplicitConversions._

import com.amazonaws.services.dynamodbv2.model._


case class PutItemExecutor[A <: AnyPutItem](
  dynamoClient: AnyDynamoDBClient,
  serializer: A#Item#Raw SerializeTo SDKRep
) extends ExecutorFor[A] {

  type OutC[X] = X

  import scala.collection.JavaConversions._

  def apply(action: A)(inputState: A#InputState): Out = {
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
