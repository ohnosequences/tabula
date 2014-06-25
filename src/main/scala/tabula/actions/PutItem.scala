package ohnosequences.tabula

import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.tabula.impl.ImplicitConversions._

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

  val getSDKRep: item.Rep => SDKRep
}

case class InTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withValue(itemRep: i.Rep)(implicit
      val transf: FromItem[i.type, SDKRep],
      val hasHashKey:  i.type HasProperty t.HashKey,
      val hasRangeKey: i.type HasProperty t.RangeKey
    ) extends AnyPutItemAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  getSDKRep = (r: i.Rep) => transf(i, r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

}
