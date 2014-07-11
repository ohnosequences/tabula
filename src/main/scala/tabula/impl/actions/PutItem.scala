package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class InTable[T <: Singleton with AnyCompositeKeyTable]
  (state: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withValue(itemRep: i.Rep)(implicit
      val transf: FromAttributes.Item[i.type, SDKRep],
      val hasHashKey:  state.resource.HashKey  ∈ i.Attributes,
      val hasRangeKey: state.resource.RangeKey ∈ i.Attributes 
    ) extends AnyPutItemAction with SDKRepGetter {
      type Table = T
      val  table = state.resource

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  getSDKRep = (r: i.Rep) => transf(r)

      val inputState = state

      override def toString = s"InTable ${state.resource.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

}
