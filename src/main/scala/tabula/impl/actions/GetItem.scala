package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class FromHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (state: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKey(hashKeyValue: state.resource.hashKey.Raw)
    (implicit
      val form: ToItem[SDKRep, i.type],
      val hasHashKey: state.resource.HashKey ∈ i.Properties
    ) extends AnyGetItemHashKeyAction with SDKRepParser {
      type Table = T
      val  table = state.resource: state.resource.type

      type Item = I
      val  item = i: i.type

      val input = hashKeyValue

      val inputState = state

      val parseSDKRep = (m: SDKRep) => form(m, i)

      override def toString = s"FromTable ${state.resource.name} getItem ${i.label} withKey ${hashKeyValue}"
    }

  }

}


/* ### Composite key table */
case class FromCompositeKeyTable[T <: Singleton with AnyCompositeKeyTable]
  (state: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKeys(
      hashKeyValue: state.resource.hashKey.Raw,
      rangeKeyValue: state.resource.rangeKey.Raw
    )(implicit
      val form: ToItem[SDKRep, i.type],
      val hasHashKey:  state.resource.HashKey  ∈ i.Properties,
      val hasRangeKey: state.resource.RangeKey ∈ i.Properties
    ) extends AnyGetItemCompositeKeyAction with SDKRepParser {
      type Table = T
      val  table = state.resource: state.resource.type

      type Item = I
      val  item = i: i.type

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = state

      val parseSDKRep = (m: SDKRep) => form(m, i)

      override def toString = s"FromTable ${state.resource.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
    }

  }

}
