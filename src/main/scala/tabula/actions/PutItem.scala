package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait PutItemResult
case class  PutItemFail(msg: String) extends PutItemResult
case object PutItemSuccess extends PutItemResult

trait AnyPutItemAction extends AnyTableItemAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = item.Rep
  type Output = PutItemResult
}

// case class PutItem[I <: Singleton with AnyItem](val i: I) {
//   case class withValue(val itemRep: i.Rep) extends AnyPutItemAction {

//     type Item = I
//     val  item = i: i.type

//     val  input = itemRep
//   }
// }
