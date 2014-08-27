package ohnosequences.tabula.impl.actions

import ohnosequences.pointless._, AnyTypeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class QueryTable[T <: Singleton with AnyCompositeKeyTable]
  (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

  case class forItem[I <: Singleton with AnyItem with AnyItem.ofTable[T]](val i: I) {

    case class withHashKey(hashKeyValue: T#HashKey#Raw)
    (implicit 
      val parser: ToItem[SDKRep, I], 
      val hasHashKey: T#HashKey ∈ I#Record#Properties
    ) 
    extends AnySimpleQueryAction with SDKRepParser { self =>

      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = SimplePredicate(item, EQ(table.hashKey, hashKeyValue))

      val inputState = inputSt
      val parseSDKRep = (m: SDKRep) => parser(m, i)

      override def toString = s"QueryTable ${t.name} forItem ${i.toString} withHashKey ${hashKeyValue}"

      case class andRangeCondition[C <: Condition.On[T#RangeKey] with KeyCondition](c: C)
          extends AnyNormalQueryAction
             with SDKRepParser {

          type Table = T
          val  table = t: t.type

          type Item = I
          val  item = i: i.type

          type RangeCondition = C
          val  rangeCondition = c

          val input = AND(SimplePredicate(item, EQ(table.hashKey, hashKeyValue)), c)

          val inputState = inputSt
          val parseSDKRep = (m: SDKRep) => parser(m, i)
          val hasHashKey = self.hasHashKey

          override def toString = s"QueryTable ${t.name} forItem ${i.toString} withHashKey ${hashKeyValue} andRangeCondition ${rangeCondition}"
      }
    }
  }
}
