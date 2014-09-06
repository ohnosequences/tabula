package ohnosequences.tabula

import ohnosequences.pointless._, AnyType._, AnyTypeSet._

import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, Condition._, AnyItem._
import ohnosequences.tabula.impl.ImplicitConversions._

// sealed trait AnyQueryResult { type Item <: AnyItem }
// abstract class QueryResult[I <: AnyItem] extends AnyQueryResult { type Item = I }
// case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult[I]
// case class QuerySuccess[I <: AnyItem](item: List[ValueOf[I]]) extends QueryResult[I]

/* ### Common action trait */
sealed trait AnyQuery extends AnyItemAction {
  // quieries make sense only for the composite key tables
  type Item <: AnyItem.OfCompositeTable

  //require updating or creating
  type InputState  = AnyTableState.For[TableOf[Item]] with ReadyTable
  type OutputState = InputState

  // TODO: restrict this type better
  type Predicate <: AnyPredicate.On[Item]
  val  predicate: Predicate

  type Output = List[ValueOf[Item]]
}

sealed trait QueryFor[I <: AnyItem.OfCompositeTable] extends AnyQuery {
  type Item = I
  // type Output = List[ValueOf[I]]
}

// object AnyQuery {
//   type Q[A <: AnyQuery] = QueryFor[A#Item]
// }

case class SimpleQuery[
  I <: AnyItem.OfCompositeTable,
  H <: RawOf[TableOf[I]#PrimaryKey#Hash]
](i: I, h: H) extends QueryFor[I] {
  val  item = i

  type Predicate = SimplePredicate[I, EQ[TableOf[I]#PrimaryKey#Hash]] 
  val  predicate = SimplePredicate(item, EQ(item.table.primaryKey.hash, h))
  // val  predicate = p
}

// the range key condition is optional
case class NormalQuery[
  I <: AnyItem.OfCompositeTable, 
  P <: SimplePredicate[I, EQ[TableOf[I]#PrimaryKey#Hash]],
  R <: Condition.On[TableOf[I]#PrimaryKey#Range] with KeyCondition
](p: P, r: R) extends QueryFor[I] {
  
  type Predicate = AND[P, R]
  val  predicate = AND[P, R](p, r)
  val  item = p.item
}
