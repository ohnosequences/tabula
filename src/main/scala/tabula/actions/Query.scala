package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait QueryResult { type Item <: AnyItem }
case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult { type Item = I }
case class QuerySuccess[I <: Singleton with AnyItem](item: List[I#Raw]) extends QueryResult { type Item = I }

/* ### Common action trait */
trait AnyQueryAction extends AnyTableItemAction { action =>
  // quieries make sense only for the composite key tables
  type Table <: Singleton with AnyCompositeKeyTable

  // TODO: restrict this type better
  type KeyConditions <: AnyPredicate.On[Item]
  val  keyConditions: KeyConditions

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = QueryResult
}

trait AnySimpleQueryAction extends AnyQueryAction {
  type Input = table.hashKey.Raw

  type KeyConditions = SimplePredicate[Item, EQ[table.HashKey]]
  val  keyConditions = SimplePredicate(item, EQ(table.hashKey, input))
}

// the range key condition is optional
trait AnyNormalQueryAction extends AnyQueryAction {
  type Input = table.hashKey.Raw

  type RangeCondition <: Condition.On[table.RangeKey] with KeyCondition
  val  rangeCondition: RangeCondition

  type KeyConditions = AND[SimplePredicate[Item, EQ[table.HashKey]], RangeCondition]
  val  keyConditions = AND(SimplePredicate(item, EQ(table.hashKey, input)), rangeCondition)
}
