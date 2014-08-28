package ohnosequences.tabula.impl.actions

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class FromHashKeyTable[T <: AnyHashKeyTable]
  (val table: T, val inputSt: AnyTableState.For[T] with ReadyTable) { fromHashKeyTable =>

  case class getItem[I <: AnyItem with AnyItem.ofTable[T]](val item: I) { getItem =>

    case class withKey (
      hashKeyValue: RawOf[T#HashKey]
    )
    (implicit
      val form: ToItem[SDKRep, I],
      val hasHashKey: T#HashKey ∈ I#Properties
    ) 
    extends AnyGetItemHashKeyAction with SDKRepParser {

      type Table = T
      val table = fromHashKeyTable.table

      type Item = I
      val item = getItem.item

      val input = hashKeyValue

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, item)

      override def toString = s"FromTable ${table.name} getItem ${item.label} withKey ${hashKeyValue}"
    }

  }

}


/* ### Composite key table */
case class FromCompositeKeyTable[T <: AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: AnyItem.ofTable[T]](i: I) {

    case class withKeys(
      hashKeyValue: RawOf[T#HashKey],
      rangeKeyValue: RawOf[T#RangeKey]
    )(implicit
      val form: ToItem[SDKRep, I],
      val hasHashKey:  T#HashKey  ∈ I#Properties,
      val hasRangeKey: T#RangeKey ∈ I#Properties
    ) 
    extends AnyGetItemCompositeKeyAction with SDKRepParser {
      
      type Table = T
      val  table = t

      type Item = I
      val  item = i

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, i)

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
    }

  }

}
