package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyTableItemAction._

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait PutItemResult
case class  PutItemFail(msg: String) extends PutItemResult
case object PutItemSuccess extends PutItemResult

trait AnyPutItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = Tagged[Item]
  val  input: Input

  type Output = PutItemResult
}

case class PutItem[I <: AnyItem](val itemRep: Tagged[I])
  (implicit val getI: Tagged[I] => I) extends AnyPutItemAction {

    type Item = I

    type Table = AnyItem.TableOf[I]
    val  item = getI(itemRep)

    val  input = itemRep
  }
