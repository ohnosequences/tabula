package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait AnyGetItemResult { type Item <: AnyItem }
abstract class GetItemResult[I <: AnyItem] extends AnyGetItemResult { type Item = I }
case class GetItemFailure[I <: AnyItem](msg: String) extends GetItemResult[I]
case class GetItemSuccess[I <: AnyItem](item: Tagged[I]) extends GetItemResult[I]

/* ### Common action trait */
trait AnyGetItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = GetItemResult[Item]
}


/* ### Hash key table */
trait AnyGetItemHashKeyAction extends AnyGetItemAction {

  type Table <: AnyHashKeyTable
  type Input = RawOf[Table#HashKey]
}

/* ### Composite key table */
trait AnyGetItemCompositeKeyAction extends AnyGetItemAction {

  type Table <: AnyCompositeKeyTable
  type Input = ( RawOf[Table#HashKey], RawOf[Table#RangeKey] )
}
