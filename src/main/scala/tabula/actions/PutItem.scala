package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyItemAction._

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.impl.ImplicitConversions._
import ohnosequences.tabula._, AnyItem._

trait AnyPutItemAction extends AnyItemAction {
  //require updating or creating
  type InputState  <: AnyTableState.For[TableOf[Item]] with ReadyTable
  type OutputState <: InputState

  type ItemValue <: Tagged[Item]
  val  itemValue: ItemValue

  type Output = None.type
}

case class PutItem[I <: AnyItem](val itemValue: Tagged[I])
  (implicit val getI: Tagged[I] => I) extends AnyPutItemAction {

  type Item = I
  val  item = getI(itemValue)

  type ItemValue = Tagged[I]

  type InputState  = AnyTableState.For[TableOf[I]] with ReadyTable
  type OutputState = InputState
}
