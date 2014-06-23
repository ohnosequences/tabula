package ohnosequences.tabula.impl

import ohnosequences.tabula._
import ohnosequences.tabula.impl.AttributeImplicits._
import ohnosequences.tabula.ExecutorResult

case class UpdateItemCompositeKeyExecutor[A <: AnyUpdateItemCompositeKeyAction](a: A)(
  dynamoClient: AnyDynamoDBClient
  ) extends Executor[A](a) {

  type OutC[X] = X

  import scala.collection.JavaConversions._
  def apply(): Out = {
    println("executing: " + action)

   // try {
      dynamoClient.client.updateItem (
        action.table.name,
        Map(
          action.table.hashKey.label -> getAttrVal(action.input._1),
          action.table.rangeKey.label -> getAttrVal(action.input._2)
        ),
        a.input._3
      )
   // } catch {
    //  case t: Throwable => println(t.printStackTrace)
   // }
    ExecutorResult(None, action.table, action.inputState)
  }
}
