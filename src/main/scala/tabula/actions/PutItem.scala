package ohnosequences.tabula

import ohnosequences.pointless._, AnyType._, AnyItemAction._

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.impl.ImplicitConversions._
import ohnosequences.tabula._, AnyItem._

trait AnyPutItem extends AnyItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[TableOf[Item]] with ReadyTable
  type OutputState = InputState

  // type ItemValue = ValueOf[Item]
  val  itemValue: ValueOf[Item]

  type Output = None.type
}

case class PutItem[I <: AnyItem](val itemValue: ValueOf[I])
  (implicit val getI: ValueOf[I] => I) extends AnyPutItem {

  type Item = I
  val  item = getI(itemValue)

  // type ItemValue = ValueOf[I]
}
