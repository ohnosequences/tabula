package ohnosequences.tabula.action

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}

import ohnosequences.cosas._, types._, typeSets._

import ohnosequences.tabula._, states._, actions._, conditions._, predicates._, items._, tables._
import ohnosequences.tabula.impl.ImplicitConversions._

// sealed trait AnyQueryResult { type Item <: AnyItem }
// abstract class QueryResult[I <: AnyItem] extends AnyQueryResult { type Item = I }
// case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult[I]
// case class QuerySuccess[I <: AnyItem](item: List[ValueOf[I]]) extends QueryResult[I]

/* ### Common action trait */
sealed trait AnyQuery extends AnyItemAction {
  // quieries make sense only for the composite key tables
  type Item <: AnyItem.ofCompositeTable

  //require updating or creating
  type InputState  <: AnyTableState.For[Item#Table] with ReadyTable
  type OutputState = InputState

  // TODO: restrict this type better
  type Predicate <: AnyPredicate.On[Item]
  val  predicate: Predicate

  type Output = List[ValueOf[Item]]
  // type Output = List[AnyValue.ofType[Item]]
}

sealed trait QueryFor[I <: AnyItem.ofCompositeTable] extends AnyQuery {

  type Item = I
}

// object AnyQuery {
//   type Q[A <: AnyQuery] = QueryFor[A#Item]
// }

case class SimpleQuery[
  I <: AnyItem.ofCompositeTable,
  H <: I#Table#PrimaryKey#Hash#Raw
](i: I, h: H) extends QueryFor[I] {

  val  item = i

  type Predicate = SimplePredicate[Item, EQ[Item#Table#PrimaryKey#Hash]]
  // FIXME: some types problem here
  val  predicate = ??? //SimplePredicate(item, EQ(item.table.primaryKey.hash, h))
  // val  predicate = p
}

// the range key condition is optional
case class NormalQuery[
  I <: AnyItem.ofCompositeTable,
  P <: SimplePredicate[I, EQ[I#Table#PrimaryKey#Hash]],
  R <: AnyCondition.On[I#Table#PrimaryKey#Range] with AnyKeyCondition
](p: P, r: R) extends QueryFor[I] {

  type Predicate = AND[P, R]
  val  predicate = AND[P, R](p, r)
  val  item = p.item
}
