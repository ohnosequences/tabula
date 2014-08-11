package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class FromHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (val table: T, val inputSt: AnyTableState.For[T] with ReadyTable) { fromHashKeyTable =>

  case class getItem[I <: Singleton with AnyItem with AnyItem.ofTable[T]](val item: I) { getItem =>

    case class withKey (
      hashKeyValue: table.hashKey.Raw
    )
    (implicit
      val form: ToItem[SDKRep, item.type],
      val hasHashKey: table.HashKey ∈ item.record.Properties
    ) extends AnyGetItemHashKeyAction with SDKRepParser {

      type Table = T
      val table = fromHashKeyTable.table: fromHashKeyTable.table.type

      type Item = I
      val item = getItem.item: getItem.item.type

      val input = hashKeyValue

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, item: item.type)

      override def toString = s"FromTable ${table.name} getItem ${item.label} withKey ${hashKeyValue}"
    }

  }

}


/* ### Composite key table */
case class FromCompositeKeyTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKeys(
      hashKeyValue: t.hashKey.Raw,
      rangeKeyValue: t.rangeKey.Raw
    )(implicit
      val form: ToItem[SDKRep, i.type],
      val hasHashKey:  t.HashKey  ∈ i.record.Properties,
      val hasRangeKey: t.RangeKey ∈ i.record.Properties
    ) extends AnyGetItemCompositeKeyAction with SDKRepParser {
      
      type Table = T
      val  table = t

      type Item = I
      val  item = i:i.type

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, i:item.type)

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
    }

  }

}
