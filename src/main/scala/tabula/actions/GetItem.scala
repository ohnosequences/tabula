package ohnosequences.tabula

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait AnyGetItemResult { type Item <: AnyItem }
abstract class GetItemResult[I <: AnyItem] extends AnyGetItemResult { type Item = I }
case class GetItemFailure[I <: Singleton with AnyItem](msg: String) extends GetItemResult[I]
case class GetItemSuccess[I <: Singleton with AnyItem](item: I#Rep) extends GetItemResult[I]

/* ### Common action trait */
trait AnyGetItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = GetItemResult[Item]
}


/* ### Hash key table */
trait AnyGetItemHashKeyAction extends AnyGetItemAction {
  type Table <: Singleton with AnyHashKeyTable
  type Input = Table#HashKey#Raw
}

/* ### Composite key table */
trait AnyGetItemCompositeKeyAction extends AnyGetItemAction {
  type Table <: Singleton with AnyCompositeKeyTable
  type Input = (Table#HashKey#Raw, Table#RangeKey#Raw)
}
