package ohnosequences.tabula

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}

/* ### Common action trait */
trait AnyUpdateItemAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Output = None.type

}


/* ### Hash key table */
trait AnyUpdateItemHashKeyAction extends AnyUpdateItemAction {
  type Table <: Singleton with AnyHashKeyTable
  //todo change it to abstract update
  type Input = (table.hashKey.Raw, Map[String, AttributeValueUpdate])
}

/* ### Composite key table */
trait AnyUpdateItemCompositeKeyAction extends AnyUpdateItemAction {
  type Table <: Singleton with AnyCompositeKeyTable
  type Input = (table.hashKey.Raw, table.rangeKey.Raw, Map[String, AttributeValueUpdate])
}
