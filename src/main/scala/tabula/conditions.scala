package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType.{RawOf, Tagged}, AnyTypeUnion._

/*
  ## Conditions

  Conditions represent the selection criteria for a Query or Scan operation. See

  - [Conditions API Reference](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Condition.html)
  - [Simple Conditions Documentation](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithItems.html#Conditions.SimpleConditions)

  * Comparison Operators with **No** Property Values:
    `NOT_NULL | NULL`
  * Comparison Operators with **One** Property Value:  
    `EQ | NE | LE | LT | GE | GT | CONTAINS | NOT_CONTAINS | BEGINS_WITH`
  * Comparison Operators with **Two** Property Values:  
    `BETWEEN`
  * Comparison Operators with **N** Property Values:
    `IN`
*/
trait Condition {
  
  type Property <: AnyProperty
  val  property: Property
}

sealed trait KeyCondition extends Condition

object Condition {
  type On[A <: AnyProperty] = Condition { type Property = A }

  implicit def conditionAnyOps[A <: AnyProperty](property: A):
      ConditionAnyOps[A] = 
      ConditionAnyOps[A](property)

  implicit def conditionNotSetOps[A <: AnyProperty](property: A)
    (implicit ev: RawOf[A] isOneOf NotSetValues): 
      ConditionNotSetOps[A] = 
      ConditionNotSetOps[A](property)

  implicit def conditionWithPrefixOps[A <: AnyProperty](property: A)
    (implicit ev: RawOf[A] isOneOf ValuesWithPrefixes): 
      ConditionWithPrefixOps[A] = 
      ConditionWithPrefixOps[A](property)

  implicit def conditionSetOps[A <: AnyProperty](property: A)
    (implicit ev: RawOf[A] isOneOf SetValues): 
      ConditionSetOps[A] = 
      ConditionSetOps[A](property)

  // implicit def toSet[A <: AnyProperty, V](v: V)(implicit eq: Set[V] =:= RawOf[A]): RawOf[A] = eq(Set[V](v))
}


/*
  ### Comparison Operators with **No** Property Values
*/
sealed trait NullaryCondition[A <: AnyProperty] 
  extends Condition { type Property = A }

/* - `NOT_NULL` - true if an property exists */
case class     NULL[A <: AnyProperty](val property: A) extends NullaryCondition[A]
/* - `NULL` - true if an property does not exist */
case class NOT_NULL[A <: AnyProperty](val property: A) extends NullaryCondition[A]


/*
  ## Comparison Operators with **One** Property Value
*/
trait SimpleCondition[A <: AnyProperty] extends Condition {

  type Property = A
  val value: A#Raw
}

/* - `EQ` - true if an property is equal to a value */
case class EQ[A <: AnyProperty](
  val property: A,
  val value: A#Raw
) extends SimpleCondition[A] with KeyCondition

/* - `NE` - true if an property is not equal to a value */
// NOTE: this is not a KeyCondition for some reason
case class NE[A <: AnyProperty](
  val property: A,
  val value: RawOf[A]
) extends SimpleCondition[A]

/* - `LE` - true if an property is less than or equal to a value */
case class LE[A <: AnyProperty](
  val property: A,
  val value: RawOf[A]
)(implicit 
  ev: RawOf[A] isOneOf NotSetValues
) extends SimpleCondition[A] with KeyCondition

/* - `LT` - true if an property is less than a value */
case class LT[A <: AnyProperty](
  val property: A,
  val value: RawOf[A]
)(implicit ev: RawOf[A] isOneOf NotSetValues)
  extends SimpleCondition[A] with KeyCondition

/* - `GE` - true if an property is greater than or equal to a value */
case class GE[A <: AnyProperty](
  val property: A,
  val value: RawOf[A]
)(implicit
  ev: RawOf[A] isOneOf NotSetValues
) extends SimpleCondition[A] with KeyCondition

/* - `GT` - true if an property is greater than a value */
case class GT[A <: AnyProperty](
  val property: A,
  val value: RawOf[A]
)(implicit 
  ev: RawOf[A] isOneOf NotSetValues
) extends SimpleCondition[A] with KeyCondition


/* - `CONTAINS` - true if a value is present within a set, or if one value contains another */
// NOTE: the value here is a set!
case class CONTAINS[A <: AnyProperty, V](
  val property: A, 
  val value: V
)(implicit 
  ev: RawOf[A] isOneOf SetValues,
  eq: Set[V] =:= RawOf[A]
) extends Condition { type Property = A }

/* - `NOT_CONTAINS` - true if a value is not present within a set, or if one value does not contain another value */
case class NOT_CONTAINS[A <: AnyProperty, V](
  val property: A, 
  val value: V
)(implicit 
  ev: RawOf[A] isOneOf SetValues,
  eq: Set[V] =:= RawOf[A]
) extends Condition { type Property = A }

/* - `BEGINS_WITH` - true if the first few characters of an property match the provided value. Do not use this operator for comparing numbers */
case class BEGINS_WITH[A <: AnyProperty](
  val property: A, 
  val value: RawOf[A]
)(implicit 
  ev: RawOf[A] isOneOf ValuesWithPrefixes
) extends SimpleCondition[A] with KeyCondition


/*
  ## Comparison Operators with **Two** Property Values
*/    

/* - `BETWEEN` - true if a value is between a lower bound and an upper bound, endpoints inclusive */
case class BETWEEN[A <: AnyProperty](
  val property: A,
  val start: RawOf[A],
  val end: RawOf[A]
)(implicit
  ev: RawOf[A] isOneOf NotSetValues
) extends KeyCondition { type Property = A }

// NOTE: this is not in the Amazon documentation
// case class NOT_BETWEEN[A <: AnyProperty](
//   val property: A,
//   val start: RawOf[A],
//   val end: RawOf[A]
// )(implicit
//   ev: RawOf[A] isOneOf NotSetValues
// ) extends KeyCondition { type Property = A }


/*
  ## Comparison Operators with **N** Property Values:
*/

/* - `IN` - true if a value is equal to any of the values in an enumerated list. 
      Only scalar values are supported in the list, not sets. The target property 
      must be of the same type and exact value in order to match */
case class IN[A <: AnyProperty](
  val property: A,
  val values: List[RawOf[A]]
)(implicit
  ev: RawOf[A] isOneOf NotSetValues
) extends Condition { type Property = A }


/* ## Method aliases for condition constructors */
case class ConditionAnyOps[A <: AnyProperty](property: A) {
  final def isThere  = NOT_NULL(property)
  final def notThere =     NULL(property)

  final def ===(value: RawOf[A]): EQ[A] = EQ(property, value)
  final def  eq(value: RawOf[A]): EQ[A] = EQ(property, value)
}

case class ConditionWithPrefixOps[A <: AnyProperty](property: A)
    (implicit ev: RawOf[A] isOneOf ValuesWithPrefixes) {

  final def beginsWith(value: RawOf[A]): BEGINS_WITH[A] = BEGINS_WITH(property, value)
}

case class ConditionNotSetOps[A <: AnyProperty](property: A)
    (implicit ev: RawOf[A] isOneOf NotSetValues) {

  final def <(value: RawOf[A]): LT[A] = LT(property, value)
  final def ≤(value: RawOf[A]): LE[A] = LE(property, value)
  final def >(value: RawOf[A]): GT[A] = GT(property, value)
  final def ≥(value: RawOf[A]): GE[A] = GE(property, value)

  // non-symbolic names:
  final def lt(value: RawOf[A]): LT[A] = LT(property, value)
  final def le(value: RawOf[A]): LE[A] = LE(property, value)
  final def gt(value: RawOf[A]): GT[A] = GT(property, value)
  final def ge(value: RawOf[A]): GE[A] = GE(property, value)

  final def    between(start: RawOf[A], end: RawOf[A]):     BETWEEN[A] =     BETWEEN(property, start, end)
  // final def notBetween(start: RawOf[A], end: RawOf[A]): NOT_BETWEEN[A] = NOT_BETWEEN(property, start, end)

  final def isOneOf(values: List[RawOf[A]]): IN[A] = IN(property, values)
  final def      in(values: List[RawOf[A]]): IN[A] = IN(property, values)
}

case class ConditionSetOps[A <: AnyProperty](property: A)
    (implicit ev: RawOf[A] isOneOf SetValues) {

  final def ∋[V](value: V)(implicit eq: Set[V] =:= RawOf[A]):     CONTAINS[A, V] =     CONTAINS(property, value)
  final def ∌[V](value: V)(implicit eq: Set[V] =:= RawOf[A]): NOT_CONTAINS[A, V] = NOT_CONTAINS(property, value)
}
