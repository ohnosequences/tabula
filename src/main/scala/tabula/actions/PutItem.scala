package ohnosequences.tabula.action

import ohnosequences.cosas.types._
import ohnosequences.tabula._, states._, actions._, items._


case class PutItem[I <: AnyItem]
  (val itemValue: ValueOf[I])
  (implicit val getI: ValueOf[I] => I)
extends AnyItemAction {

  type Item = I
  val  item = getI(itemValue)

  //require updating or creating
  type InputState  = AnyTableState.For[Item#Table] with ReadyTable
  type OutputState = InputState

  // type ItemValue = ValueOf[Item]

  type Output = None.type
}
