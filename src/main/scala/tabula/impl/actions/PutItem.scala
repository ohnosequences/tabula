package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class InHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem with AnyItem.ofTable[T]](i: I) {

    case class withValue(
      val itemRep: TaggedWith[I]
    )(implicit
      val transf: From.Item[I, SDKRep],
      val hasHashKey:  T#HashKey ∈ I#Record#Properties
    ) 
    extends AnyPutItemAction with SDKRepGetter {

      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  getSDKRep = (r: I#Rep) => transf(r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

}


case class InCompositeKeyTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withValue(itemRep: TaggedWith[I])(implicit
      val transf: From.Item[I, SDKRep],
      val hasHashKey:  t.HashKey  ∈ i.record.Properties,
      val hasRangeKey: t.RangeKey ∈ i.record.Properties 
    ) extends AnyPutItemAction with SDKRepGetter {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  getSDKRep = (r: I#Rep) => transf(r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

}
