package ohnosequences.tabula.impl.actions

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class InHashKeyTable[T <: AnyHashKeyTable]
  (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: AnyItem with AnyItem.ofTable[T]](val i: I) {

    case class withValue(
      val itemRep: Tagged[I]
    )(implicit
      val transf: From.Item[I, SDKRep],
      val hasHashKey:  T#HashKey ∈ I#Properties
    ) 
    extends AnyPutItemAction with SDKRepGetter {

      type Table = T
      val  table = t

      type Item = I
      val  item = i

      val  input = itemRep
      val  getSDKRep = (r:Tagged[I]) => transf(r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.toString} withValue ${itemRep}"
    }

  }

}


case class InCompositeKeyTable[T <: AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <:AnyItem.ofTable[T]](i: I) {

    case class withValue(itemRep: Tagged[I])(implicit
      val transf: From.Item[I, SDKRep],
      val hasHashKey:  T#HashKey  ∈ I#Properties,
      val hasRangeKey: T#RangeKey ∈ I#Properties 
    ) 
    extends AnyPutItemAction with SDKRepGetter {
      type Table = T
      val  table = t

      type Item = I
      val  item = i

      val  input = itemRep
      val  getSDKRep = (r: Tagged[I]) => transf(r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.toString} withValue ${itemRep}"
    }

  }

}
