package ohnosequences.tabula.action

import ohnosequences.cosas._, types._
import ohnosequences.tabula._, items._, actions._, states._

case class GetItem[I <: AnyItem](
  val item: I,
  val keyValue: I#Table#PrimaryKey#Raw
) extends AnyItemAction {

  type Item = I

  // require updating or creating
  type InputState  = AnyTableState.For[TableOf[Item]] with ReadyTable
  type OutputState = InputState

  type Output = ValueOf[Item]
}
