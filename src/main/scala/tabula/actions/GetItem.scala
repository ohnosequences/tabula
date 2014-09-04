package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

trait AnyGetItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = PrimaryKeyValue[Table#PrimaryKey]
  type Output = Tagged[Item]
}
