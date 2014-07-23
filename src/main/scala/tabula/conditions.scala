package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._

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
  
  type Property <: Singleton with AnyProperty
  val  property: Property
}

sealed trait KeyCondition extends Condition

object Condition {
  type On[A <: Singleton with AnyProperty] = Condition { type Property = A }

  implicit def conditionAnyOps[A <: Singleton with AnyProperty](property: A):
      ConditionAnyOps[A] = 
      ConditionAnyOps[A](property)

  implicit def conditionNotSetOps[A <: Singleton with AnyProperty](property: A)
    (implicit ev: A#Raw :<: NotSetValues): 
      ConditionNotSetOps[A] = 
      ConditionNotSetOps[A](property)

  implicit def conditionWithPrefixOps[A <: Singleton with AnyProperty](property: A)
    (implicit ev: A#Raw :<: ValuesWithPrefixes): 
      ConditionWithPrefixOps[A] = 
      ConditionWithPrefixOps[A](property)

  implicit def conditionSetOps[A <: Singleton with AnyProperty](property: A)
    (implicit ev: A#Raw :<: SetValues): 
      ConditionSetOps[A] = 
      ConditionSetOps[A](property)

  // implicit def toSet[A <: Singleton with AnyProperty, V](v: V)(implicit eq: Set[V] =:= A#Raw): A#Raw = eq(Set[V](v))
}


/*
  ### Comparison Operators with **No** Property Values
*/
sealed trait NullaryCondition[A <: Singleton with AnyProperty] 
  extends Condition { type Property = A }

/* - `NOT_NULL` - true if an property exists */
case class     NULL[A <: Singleton with AnyProperty](val property: A) extends NullaryCondition[A]
/* - `NULL` - true if an property does not exist */
case class NOT_NULL[A <: Singleton with AnyProperty](val property: A) extends NullaryCondition[A]


/*
  ## Comparison Operators with **One** Property Value
*/
trait SimpleCondition[A <: Singleton with AnyProperty] extends Condition {

  type Property = A
  val value: A#Raw
}

/* - `EQ` - true if an property is equal to a value */
case class EQ[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
) extends SimpleCondition[A] with KeyCondition

/* - `NE` - true if an property is not equal to a value */
// NOTE: this is not a KeyCondition for some reason
case class NE[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
) extends SimpleCondition[A]

/* - `LE` - true if an property is less than or equal to a value */
case class LE[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition

/* - `LT` - true if an property is less than a value */
case class LT[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit ev: A#Raw :<: NotSetValues)
  extends SimpleCondition[A] with KeyCondition

/* - `GE` - true if an property is greater than or equal to a value */
case class GE[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition

/* - `GT` - true if an property is greater than a value */
case class GT[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition


/* - `CONTAINS` - true if a value is present within a set, or if one value contains another */
// NOTE: the value here is a set!
case class CONTAINS[A <: Singleton with AnyProperty, V](
  val property: A, 
  val value: V
)(implicit 
  ev: A#Raw :<: SetValues,
  eq: Set[V] =:= A#Raw
) extends Condition { type Property = A }

/* - `NOT_CONTAINS` - true if a value is not present within a set, or if one value does not contain another value */
case class NOT_CONTAINS[A <: Singleton with AnyProperty, V](
  val property: A, 
  val value: V
)(implicit 
  ev: A#Raw :<: SetValues,
  eq: Set[V] =:= A#Raw
) extends Condition { type Property = A }

/* - `BEGINS_WITH` - true if the first few characters of an property match the provided value. Do not use this operator for comparing numbers */
case class BEGINS_WITH[A <: Singleton with AnyProperty](
  val property: A, 
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: ValuesWithPrefixes
) extends SimpleCondition[A] with KeyCondition


/*
  ## Comparison Operators with **Two** Property Values
*/    

/* - `BETWEEN` - true if a value is between a lower bound and an upper bound, endpoints inclusive */
case class BETWEEN[A <: Singleton with AnyProperty](
  val property: A,
  val start: A#Raw,
  val end: A#Raw
)(implicit
  ev: A#Raw :<: NotSetValues
) extends KeyCondition { type Property = A }

// NOTE: this is not in the Amazon documentation
// case class NOT_BETWEEN[A <: Singleton with AnyProperty](
//   val property: A,
//   val start: A#Raw,
//   val end: A#Raw
// )(implicit
//   ev: A#Raw :<: NotSetValues
// ) extends KeyCondition { type Property = A }


/*
  ## Comparison Operators with **N** Property Values:
*/

/* - `IN` - true if a value is equal to any of the values in an enumerated list. 
      Only scalar values are supported in the list, not sets. The target property 
      must be of the same type and exact value in order to match */
case class IN[A <: Singleton with AnyProperty](
  val property: A,
  val values: List[A#Raw]
)(implicit
  ev: A#Raw :<: NotSetValues
) extends Condition { type Property = A }


/* ## Method aliases for condition constructors */
case class ConditionAnyOps[A <: Singleton with AnyProperty](property: A) {
  final def isThere  = NOT_NULL(property)
  final def notThere =     NULL(property)

  final def ===(value: A#Raw): EQ[A] = EQ(property, value)
  final def  eq(value: A#Raw): EQ[A] = EQ(property, value)
}

case class ConditionWithPrefixOps[A <: Singleton with AnyProperty](property: A)
    (implicit ev: A#Raw :<: ValuesWithPrefixes) {

  final def beginsWith(value: A#Raw): BEGINS_WITH[A] = BEGINS_WITH(property, value)
}

case class ConditionNotSetOps[A <: Singleton with AnyProperty](property: A)
    (implicit ev: A#Raw :<: NotSetValues) {

  final def <(value: A#Raw): LT[A] = LT(property, value)
  final def ≤(value: A#Raw): LE[A] = LE(property, value)
  final def >(value: A#Raw): GT[A] = GT(property, value)
  final def ≥(value: A#Raw): GE[A] = GE(property, value)

  // non-symbolic names:
  final def lt(value: A#Raw): LT[A] = LT(property, value)
  final def le(value: A#Raw): LE[A] = LE(property, value)
  final def gt(value: A#Raw): GT[A] = GT(property, value)
  final def ge(value: A#Raw): GE[A] = GE(property, value)

  final def    between(start: A#Raw, end: A#Raw):     BETWEEN[A] =     BETWEEN(property, start, end)
  // final def notBetween(start: A#Raw, end: A#Raw): NOT_BETWEEN[A] = NOT_BETWEEN(property, start, end)

  final def isOneOf(values: List[A#Raw]): IN[A] = IN(property, values)
  final def      in(values: List[A#Raw]): IN[A] = IN(property, values)
}

case class ConditionSetOps[A <: Singleton with AnyProperty](property: A)
    (implicit ev: A#Raw :<: SetValues) {

  final def ∋[V](value: V)(implicit eq: Set[V] =:= A#Raw):     CONTAINS[A, V] =     CONTAINS(property, value)
  final def ∌[V](value: V)(implicit eq: Set[V] =:= A#Raw): NOT_CONTAINS[A, V] = NOT_CONTAINS(property, value)
}
