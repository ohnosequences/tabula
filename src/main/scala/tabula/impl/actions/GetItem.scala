package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class FromHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem with AnyItem.ofTable[T]](val i: I) {

    case class withKey (
      hashKeyValue: t.hashKey.Raw
    )
    (implicit
      val form: ToItem[SDKRep, I],
      val hasHashKey: T#HashKey ∈ I#Record#Properties
    ) extends AnyGetItemHashKeyAction with SDKRepParser {

      type Table = T
      val  table = t

      type Item = I
      val  item = i

      val input = hashKeyValue

      val inputState = inputSt

      val parseSDKRep = (m: SDKRep) => form(m, i)

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKey ${hashKeyValue}"
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
      val form: ToItem[SDKRep, I],
      val hasHashKey:  t.HashKey  ∈ i.record.Properties,
      val hasRangeKey: t.RangeKey ∈ i.record.Properties
    ) extends AnyGetItemCompositeKeyAction with SDKRepParser {
      
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
