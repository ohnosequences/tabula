package ohnosequences.tabula

import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait GetItemResult { type Item <: AnyItem }
case class GetItemFailure[I <: AnyItem]() extends GetItemResult { type Item = I }
case class GetItemSuccess[I <: Singleton with AnyItem](item: I#Raw) extends GetItemResult { type Item = I }

/* ### Common action trait */
trait AnyGetItemAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Output = GetItemResult

  val parseSDKRep: SDKRep => item.Rep
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
      val form: ToItem[SDKRep, i.type],
      val hasHashKey:  i.type HasProperty t.HashKey
    ) extends AnyGetItemHashKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = hashKeyValue

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, i)

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKey ${hashKeyValue}"
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
      val form: ToItem[SDKRep, i.type],
      val hasHashKey:  i.type HasProperty t.HashKey,
      val hasRangeKey: i.type HasProperty t.RangeKey
    ) extends AnyGetItemCompositeKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, i)

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
    }

  }

  case class updateItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKeys(
      hashKeyValue: t.hashKey.Raw,
      rangeKeyValue: t.rangeKey.Raw,
      updates: Map[String, AttributeValueUpdate]
    )(implicit
      hasHashKey:  i.type HasProperty t.HashKey,
      hasRangeKey: i.type HasProperty t.RangeKey
    ) extends AnyUpdateItemCompositeKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = (hashKeyValue, rangeKeyValue, updates)

      val inputState = inputSt


      override def toString = s"FromTable ${t.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
    }
  }


}
