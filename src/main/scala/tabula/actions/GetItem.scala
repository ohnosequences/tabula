package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait GetItemResult { type Item <: AnyItem }
case class GetItemFailure[I <: AnyItem](msg: String) extends GetItemResult { type Item = I }
case class GetItemSuccess[I <: Singleton with AnyItem](item: I#Raw) extends GetItemResult { type Item = I }

/* ### Common action trait */
trait AnyGetItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = GetItemResult
}


/* ### Hash key table */
// trait AnyGetItemHashKeyAction extends AnyGetItemAction {
//   type Table <: Singleton with AnyTable.withHashKey
//   type Input = table.hashKey.Raw
// }

// /* ### Composite key table */
// trait AnyGetItemCompositeKeyAction extends AnyGetItemAction {
//   type Table <: Singleton with AnyTable.withCompositeKey
//   type Input = (table.hashKey.Raw, table.rangeKey.Raw)
// }
