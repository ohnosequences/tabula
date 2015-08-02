package ohnosequences.tabula.impl

import ohnosequences.cosas.ops.typeSets._
import ohnosequences.tabula._, executors._, items._, tables._
import ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import ohnosequences.cosas.types._

case class GetItemExecutor[I <: AnyItem](
  dynamoClient: AnyDynamoDBClient,
  parser: (I#Attributes ParseFrom SDKRep) { type Out = I#Raw }
) extends ExecutorFor[action.GetItem[I]] {

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

    val itemValue: ValueOf[Action#Item] =
      try {
        val itemRep: SDKRep =
          dynamoClient.client.getItem(
            action.table.name,
            itemKey
          ).getItem.toMap

        action.item := parser(action.item.attributes, itemRep)
      } catch {
        // FIXME: errors handling
        case t: Exception => println(t.printStackTrace); throw(t)
      }

    ExecutorResult[Action](itemValue, inputState)
  }

}
