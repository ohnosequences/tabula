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

  type Output = List[Denotes[Item#Raw, Item]]
  // type Output = List[AnyDenotationOf[Item]]
}

abstract class QueryFor[I <: AnyItem.ofCompositeTable](val item: I)
  extends AnyQuery { type Item = I }


case class SimpleQuery[
  I <: AnyItem.ofCompositeTable,
  P <: SimplePredicate[I, EQ[I#Table#PrimaryKey#Hash]]
  // H <: I#Table#PrimaryKey#Hash#Raw
](p: P) extends QueryFor[I](p.item) {

  type Predicate = P
  val  predicate = p
}

// the range key condition is optional
case class NormalQuery[
  I <: AnyItem.ofCompositeTable,
  P <: SimplePredicate[I, EQ[I#Table#PrimaryKey#Hash]],
  R <: AnyCondition.On[I#Table#PrimaryKey#Range] with AnyKeyCondition
](p: P, r: R) extends QueryFor[I](p.item) {

  type Predicate = AND[P, R]
  val  predicate = AND[P, R](p, r)
}
