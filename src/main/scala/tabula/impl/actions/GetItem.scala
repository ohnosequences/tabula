package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class FromHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKey(hashKeyValue: t.hashKey.Raw)
    (implicit
      val form: ToItem[SDKRep, i.type],
      val hasHashKey:  i.type HasProperty t.HashKey
    ) extends AnyGetItemHashKeyAction with SDKRepParser {
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
    ) extends AnyGetItemCompositeKeyAction with SDKRepParser {
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

}
