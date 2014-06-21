package ohnosequences.tabula

import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.AttributeValue

sealed trait PutItemResult
case object PutItemFail extends PutItemResult
case object PutItemSuccess extends PutItemResult

trait AnyPutItemAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Input = item.Rep
  val  input: Input

  type Output = PutItemResult

  val getSDKRep: item.Rep => Map[String, AttributeValue]
}

case class InTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withValue(itemRep: i.Rep)(implicit
      getter: i.Rep => Map[String, AttributeValue],
      hasHashKey:  i.type HasProperty t.HashKey,
      hasRangeKey: i.type HasProperty t.RangeKey
    ) extends AnyPutItemAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  getSDKRep = getter

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

}
