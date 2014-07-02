package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class QueryTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class forItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withHashKey(hashKeyValue: t.hashKey.Raw)(implicit 
      val parser: ToItem[SDKRep, i.type], 
      val hasHashKey: t.HashKey ∈ i.Attributes
    ) extends AnySimpleQueryAction
         with SDKRepParser {

      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = hashKeyValue
      val inputState = inputSt
      val parseSDKRep = (m: SDKRep) => parser(m, i)

      override def toString = s"QueryTable ${t.name} forItem ${i.label} withHashKey ${hashKeyValue}"

      case class andRangeCondition[C <: Condition.On[t.RangeKey] with KeyCondition](c: C)
          extends AnyNormalQueryAction
             with SDKRepParser {

          type Table = T
          val  table = t: t.type

          type Item = I
          val  item = i: i.type

          type RangeCondition = C
          val  rangeCondition = c: c.type

          val input = hashKeyValue
          val inputState = inputSt
          val parseSDKRep = (m: SDKRep) => parser(m, i)

          override def toString = s"QueryTable ${t.name} forItem ${i.label} withHashKey ${hashKeyValue} andRangeCondition ${rangeCondition}"
      }
    }
  }
}
