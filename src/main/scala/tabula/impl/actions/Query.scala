package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class QueryTable[T <: Singleton with AnyCompositeKeyTable]
  (state: AnyTableState.For[T] with ReadyTable) {

  case class forItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withHashKey(hashKeyValue: state.resource.hashKey.Raw)(implicit 
      val parser: ToItem[SDKRep, i.type], 
      val hasHashKey: state.resource.HashKey ∈ i.Attributes
    ) extends AnySimpleQueryAction
         with SDKRepParser { self =>

      type Table = T
      val  table = state.resource: state.resource.type

      type Item = I
      val  item = i: i.type

      val input = SimplePredicate(item, EQ(table.hashKey, hashKeyValue))

      val inputState = state
      val parseSDKRep = (m: SDKRep) => parser(m, i)

      override def toString = s"QueryTable ${state.resource.name} forItem ${i.label} withHashKey ${hashKeyValue}"

      case class andRangeCondition[C <: Condition.On[state.resource.RangeKey] with KeyCondition](c: C)
          extends AnyNormalQueryAction
             with SDKRepParser {

          type Table = T
          val  table = state.resource: state.resource.type

          type Item = I
          val  item = i: i.type

          type RangeCondition = C
          val  rangeCondition = c: c.type

          val input = AND(SimplePredicate(item, EQ(table.hashKey, hashKeyValue)), c)

          val inputState = state
          val parseSDKRep = (m: SDKRep) => parser(m, i)
          val hasHashKey = self.hasHashKey

          override def toString = s"QueryTable ${state.resource.name} forItem ${i.label} withHashKey ${hashKeyValue} andRangeCondition ${rangeCondition}"
      }
    }
  }
}
