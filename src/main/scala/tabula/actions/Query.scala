package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, Condition._, AnyItem._
import ohnosequences.tabula.impl.ImplicitConversions._

// sealed trait AnyQueryResult { type Item <: AnyItem }
// abstract class QueryResult[I <: AnyItem] extends AnyQueryResult { type Item = I }
// case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult[I]
// case class QuerySuccess[I <: AnyItem](item: List[Tagged[I]]) extends QueryResult[I]

/* ### Common action trait */
sealed trait AnyQueryAction extends AnyItemAction {
  // quieries make sense only for the composite key tables
  type Item <: AnyItem.OfCompositeTable

  //require updating or creating
  type InputState  <: AnyTableState.For[TableOf[Item]] with ReadyTable
  type OutputState <: InputState

  // TODO: restrict this type better
  type Predicate <: AnyPredicate.On[Item]
  val  predicate: Predicate

  type Output <: List[Tagged[Item]]
}

sealed trait QueryActionFor[I <: AnyItem.OfCompositeTable] extends AnyQueryAction {
  type InputState  = AnyTableState.For[TableOf[Item]] with ReadyTable
  type OutputState = InputState
  type Item = I
  type Output = List[Tagged[I]]
}

// object AnyQueryAction {
//   type Q[A <: AnyQueryAction] = QueryActionFor[A#Item]
// }

case class SimpleQueryAction[
  I <: AnyItem.OfCompositeTable, 
  P <: SimplePredicate[I, EQ[TableOf[I]#PrimaryKey#Hash]]
](p: P) extends QueryActionFor[I] {
  type Predicate = P 
  val  predicate = p
  val  item = p.item
  // val predicate = SimplePredicate(item, EQ(table.hashKey, hashKeyValue))
}

// the range key condition is optional
case class NormalQueryAction[
  I <: AnyItem.OfCompositeTable, 
  P <: SimplePredicate[I, EQ[TableOf[I]#PrimaryKey#Hash]],
  R <: Condition.On[TableOf[I]#PrimaryKey#Range] with KeyCondition
](p: P, r: R) extends QueryActionFor[I] {
  
  type Predicate = AND[P, R]
  val  predicate = AND[P, R](p, r)
  val  item = p.item
}
