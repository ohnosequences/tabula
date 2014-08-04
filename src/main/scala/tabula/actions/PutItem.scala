package ohnosequences.tabula

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait PutItemResult
case object PutItemFail extends PutItemResult
case object PutItemSuccess extends PutItemResult

trait AnyPutItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = TaggedWith[Item]
  val  input: Input

  type Output = PutItemResult
}
