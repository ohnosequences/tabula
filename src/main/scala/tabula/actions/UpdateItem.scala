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


trait UpdateAction {
  type Attribute
}

object UpdateAction {
  type of[A] = UpdateAction { type Attribute = A}
}

trait ItemVisitor[R] {
  def visitInt(attribute: Attribute[Int], value: Int)
 //...
  def result: R
}

//in Item
//def visit(item: ItemVisitor.Of[this.type, rep: Rep)

//for getItem from map to item rep... item rep builder...

//trait ItemRepBuilder {
//
//}
//it will be just an visitor

//update
//should accept map Attribute => Value

//trait Mapping {
//  def m[A <: AnyAttribute](a: A): Option[UpdateAction.of[A]]
//}
//
//object testMapping extends Mapping {
//  def m[A <: AnyAttribute](a: A): Option[UpdateAction.of[A]] = {
//    Some(a.la)
//  }
//}
