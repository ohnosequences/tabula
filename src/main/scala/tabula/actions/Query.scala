package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.Condition._
import ohnosequences.tabula.impl.ImplicitConversions._

// sealed trait AnyQueryResult { type Item <: AnyItem }
// abstract class QueryResult[I <: AnyItem] extends AnyQueryResult { type Item = I }
// case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult[I]
// case class QuerySuccess[I <: AnyItem](item: List[Tagged[I]]) extends QueryResult[I]

/* ### Common action trait */
trait AnyQueryAction extends AnyTableItemAction {
  // quieries make sense only for the composite key tables
  // type Table <: AnyTable.withCompositeKey
  type Item <: AnyItem //{ type Table <: AnyTable.withCompositeKey }
  type Table <: AnyItem.TableOf[Item]

  // val hasHashKey: Table#HashKey ∈ Item#Properties
  // val hasHashKey: table.primaryKey.hash ∈ item.Properties
  // val hasRangeKey: table.primaryKey.range ∈ item.Properties

  //require updating or creating
  type InputState  <: AnyTableState //.For[Table] with ReadyTable
  type OutputState = InputState

  // TODO: restrict this type better
  type Input <: AnyPredicate.On[Item]
  type Output <: List[Tagged[Item]] //QueryResult[Item]
}

// trait AnySimpleQueryAction extends AnyQueryAction {
//   type Input = SimplePredicate[Item, EQ[table.primaryKey.Hash]]
// }

// // the range key condition is optional
// trait AnyNormalQueryAction extends AnyQueryAction {
  
//   type RangeCondition <: Condition.On[Table#PrimaryKey#Range] with KeyCondition
//   // type RangeCondition <: Condition.On[table.primaryKey.Range] with KeyCondition
//   val  rangeCondition: RangeCondition

//   type Input = AND[SimplePredicate[Item, EQ[Table#PrimaryKey#Hash]], RangeCondition]
//   // type Input = AND[SimplePredicate[Item, EQ[table.primaryKey.Hash]], RangeCondition]
// }
