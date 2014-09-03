package ohnosequences.tabula.impl.actions

import ohnosequences.pointless._, AnyTypeSet._, AnyRecord._, AnyFn._
import ohnosequences.pointless.ops.record._
import ohnosequences.pointless.ops.typeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class QueryTable[T <: AnyCompositeKeyTable]
  (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

  case class forItem[I <: AnyItem with AnyItem.ofTable[T]](val i: I) {

    case class withHashKey(hashKeyValue: T#HashKey#Raw)
    (implicit 
      val parser: (I#Properties ParseFrom SDKRep) with out[RawOf[I]],
      val hasHashKey: T#HashKey ∈ I#Properties
    ) 
    extends AnySimpleQueryAction with SDKRepParser { self =>

      type Table = T
      val  table = t

      type Item = I
      val  item = i

      val input = SimplePredicate(item, EQ(table.hashKey, hashKeyValue))

      val inputState = inputSt
      val parseSDKRep = (m: SDKRep) => { item =>> parser(item.properties, m) }

      override def toString = s"QueryTable ${t.name} forItem ${i.toString} withHashKey ${hashKeyValue}"

      case class andRangeCondition[C <: Condition.On[T#RangeKey] with KeyCondition](c: C)
          extends AnyNormalQueryAction
             with SDKRepParser {

          type Table = T
          val  table = t

          type Item = I
          val  item = i

          type RangeCondition = C
          val  rangeCondition = c

          val input = AND(SimplePredicate(item, EQ(table.hashKey, hashKeyValue)), c)

          val inputState = inputSt
          val parseSDKRep = (m: SDKRep) => { item =>> parser(item.properties, m) }
          val hasHashKey = self.hasHashKey

          override def toString = s"QueryTable ${t.name} forItem ${i.toString} withHashKey ${hashKeyValue} andRangeCondition ${rangeCondition}"
      }
    }
  }
}
