package ohnosequences.tabula.impl

import ohnosequences.cosas.types._
import ohnosequences.tabula._, actions._, executors._, items._, tables._
import ImplicitConversions._

case class DeleteItemExecutor[I <: AnyItem](dynamoClient: AnyDynamoDBClient)
extends ExecutorFor[action.DeleteItem[I]] {

  type OutC[X] = X

  def apply(action: Action)(inputState: Action#InputState): Out = {

    import scala.collection.JavaConversions._
    println("executing: " + action)

    val itemKey = (action.table.primaryKey, action.keyValue) match {
      case (HashKey(hash), h) => Map(
        hash.label -> getAttrVal(h)
      )
      case (CompositeKey(hash, range), (h, r)) => Map(
        hash.label -> getAttrVal(h),
        range.label -> getAttrVal(r)
      )
    }

    try {
      dynamoClient.client.deleteItem(action.table.name, itemKey)
    } catch {
      case t: Exception => println(t.printStackTrace)
    }

    ExecutorResult[Action](None, inputState)
  }
}
