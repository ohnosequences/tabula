package ohnosequences.tabula

import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.AttributeValue

sealed trait GetItemResult { type Item <: AnyItem }
case class GetItemFail[I <: AnyItem]() extends GetItemResult { type Item = I }
case class GetItemSuccess[I <: Singleton with AnyItem](item: I#Raw) extends GetItemResult { type Item = I }

/* ### Common action trait */
trait AnyGetItemAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Output = GetItemResult

  val parseSDKRep: Map[String, AttributeValue] => item.Rep
}


/* ### Hash key table */
trait AnyGetItemHashKeyAction extends AnyGetItemAction {
  type Table <: Singleton with AnyHashKeyTable
  type Input = table.hashKey.Raw
}

case class FromHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKey(hashKeyValue: t.hashKey.Raw)
    (implicit
      hasHashKey:  i.type HasProperty t.HashKey,
      parser: Map[String, AttributeValue] => i.Rep
    ) extends AnyGetItemHashKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = hashKeyValue

      val inputState = inputSt

      val parseSDKRep = parser
    }

  }

}


/* ### Composite key table */
trait AnyGetItemCompositeKeyAction extends AnyGetItemAction {
  type Table <: Singleton with AnyCompositeKeyTable
  type Input = (table.hashKey.Raw, table.rangeKey.Raw)
}

case class FromCompositeKeyTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKeys(
      hashKeyValue: t.hashKey.Raw,
      rangeKeyValue: t.rangeKey.Raw
    )(implicit
      hasHashKey:  i.type HasProperty t.HashKey,
      hasRangeKey: i.type HasProperty t.RangeKey,
      parse: Map[String, AttributeValue] => i.Rep
    ) extends AnyGetItemCompositeKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = inputSt

      val parseSDKRep = parse
    }

  }

}
