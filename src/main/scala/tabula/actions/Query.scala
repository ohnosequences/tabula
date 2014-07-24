package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.Condition._
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait QueryResult { type Item <: AnyItem }
case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult { type Item = I }
case class QuerySuccess[I <: Singleton with AnyItem](item: List[I#Raw]) extends QueryResult { type Item = I }

/* ### Common action trait */
trait AnyQueryAction extends AnyTableItemAction { action =>
  // quieries make sense only for the composite key tables
  type Table <: Singleton with AnyTable.withCompositeKey

  // val hasHashKey: table.primaryKey.hash ∈ item.Properties
  // val hasRangeKey: table.primaryKey.range ∈ item.Properties

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  // TODO: restrict this type better
  type Input <: AnyPredicate.On[Item]
  type Output = QueryResult
}

trait AnySimpleQueryAction extends AnyQueryAction {
  type Input = SimplePredicate[Item, EQ[table.primaryKey.Hash]]
}

// the range key condition is optional
trait AnyNormalQueryAction extends AnyQueryAction {
  type RangeCondition <: Condition.On[table.primaryKey.Range] with KeyCondition
  val  rangeCondition: RangeCondition

  type Input = AND[SimplePredicate[Item, EQ[table.primaryKey.Hash]], RangeCondition]
}
