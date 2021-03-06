package ohnosequences.tabula

case object conditions {

  import attributes._
  import ohnosequences.cosas._, typeUnions._

  /*
    ## Conditions

    Conditions represent the selection criteria for a Query or Scan operation. See

    - [Conditions API Reference](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Condition.html)
    - [Simple Conditions Documentation](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithItems.html#Conditions.SimpleConditions)

    * Comparison Operators with **No** Attribute Values:
      `NOT_NULL | NULL`
    * Comparison Operators with **One** Attribute Value:
      `EQ | NE | LE | LT | GE | GT | CONTAINS | NOT_CONTAINS | BEGINS_WITH`
    * Comparison Operators with **Two** Attribute Values:
      `BETWEEN`
    * Comparison Operators with **N** Attribute Values:
      `IN`
  */
  sealed trait AnyCondition {

    type Attribute <: AnyAttribute
    val  attribute: Attribute
  }

  sealed trait AnyKeyCondition extends AnyCondition

  object AnyCondition {
    type On[A <: AnyAttribute] = AnyCondition { type Attribute = A }
  }

  object syntax {

    implicit def conditionAnyOps[A <: AnyAttribute](attribute: A):
        ConditionAnyOps[A] =
        ConditionAnyOps[A](attribute)

    implicit def conditionNotSetOps[A <: AnyAttribute](attribute: A)
      (implicit ev: A#Raw isOneOf NotSetValues):
        ConditionNotSetOps[A] =
        ConditionNotSetOps[A](attribute)

    implicit def conditionWithPrefixOps[A <: AnyAttribute](attribute: A)
      (implicit ev: A#Raw isOneOf ValuesWithPrefixes):
        ConditionWithPrefixOps[A] =
        ConditionWithPrefixOps[A](attribute)

    implicit def conditionSetOps[A <: AnyAttribute](attribute: A)
      (implicit ev: A#Raw isOneOf SetValues):
        ConditionSetOps[A] =
        ConditionSetOps[A](attribute)

    // implicit def toSet[A <: AnyAttribute, V](v: V)(implicit eq: Set[V] =:= A#Raw): A#Raw = eq(Set[V](v))
  }


  /*
    ### Comparison Operators with **No** Attribute Values
  */
  sealed trait NullaryCondition[A <: AnyAttribute]
    extends AnyCondition { type Attribute = A }

  /* - `NOT_NULL` - true if an attribute exists */
  case class     NULL[A <: AnyAttribute](val attribute: A) extends NullaryCondition[A]
  /* - `NULL` - true if an attribute does not exist */
  case class NOT_NULL[A <: AnyAttribute](val attribute: A) extends NullaryCondition[A]


  /*
    ## Comparison Operators with **One** Attribute Value
  */
  trait SimpleCondition[A <: AnyAttribute] extends AnyCondition {

    type Attribute = A
    val value: A#Raw
  }

  /* - `EQ` - true if an attribute is equal to a value */
  case class EQ[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  ) extends SimpleCondition[A] with AnyKeyCondition

  /* - `NE` - true if an attribute is not equal to a value */
  // NOTE: this is not a AnyKeyCondition for some reason
  case class NE[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  ) extends SimpleCondition[A]

  /* - `LE` - true if an attribute is less than or equal to a value */
  case class LE[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  )(implicit
    ev: A#Raw isOneOf NotSetValues
  ) extends SimpleCondition[A] with AnyKeyCondition

  /* - `LT` - true if an attribute is less than a value */
  case class LT[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  )(implicit ev: A#Raw isOneOf NotSetValues)
    extends SimpleCondition[A] with AnyKeyCondition

  /* - `GE` - true if an attribute is greater than or equal to a value */
  case class GE[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  )(implicit
    ev: A#Raw isOneOf NotSetValues
  ) extends SimpleCondition[A] with AnyKeyCondition

  /* - `GT` - true if an attribute is greater than a value */
  case class GT[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  )(implicit
    ev: A#Raw isOneOf NotSetValues
  ) extends SimpleCondition[A] with AnyKeyCondition


  /* - `CONTAINS` - true if a value is present within a set, or if one value contains another */
  // NOTE: the value here is a set!
  case class CONTAINS[A <: AnyAttribute, V](
    val attribute: A,
    val value: V
  )(implicit
    ev: A#Raw isOneOf SetValues,
    eq: Set[V] =:= A#Raw
  ) extends AnyCondition { type Attribute = A }

  /* - `NOT_CONTAINS` - true if a value is not present within a set, or if one value does not contain another value */
  case class NOT_CONTAINS[A <: AnyAttribute, V](
    val attribute: A,
    val value: V
  )(implicit
    ev: A#Raw isOneOf SetValues,
    eq: Set[V] =:= A#Raw
  ) extends AnyCondition { type Attribute = A }

  /* - `BEGINS_WITH` - true if the first few characters of an attribute match the provided value. Do not use this operator for comparing numbers */
  case class BEGINS_WITH[A <: AnyAttribute](
    val attribute: A,
    val value: A#Raw
  )(implicit
    ev: A#Raw isOneOf ValuesWithPrefixes
  ) extends SimpleCondition[A] with AnyKeyCondition


  /*
    ## Comparison Operators with **Two** Attribute Values
  */

  /* - `BETWEEN` - true if a value is between a lower bound and an upper bound, endpoints inclusive */
  case class BETWEEN[A <: AnyAttribute](
    val attribute: A,
    val start: A#Raw,
    val end: A#Raw
  )(implicit
    ev: A#Raw isOneOf NotSetValues
  ) extends AnyKeyCondition { type Attribute = A }

  // NOTE: this is not in the Amazon documentation
  // case class NOT_BETWEEN[A <: AnyAttribute](
  //   val attribute: A,
  //   val start: A#Raw,
  //   val end: A#Raw
  // )(implicit
  //   ev: A#Raw isOneOf NotSetValues
  // ) extends AnyKeyCondition { type Attribute = A }


  /*
    ## Comparison Operators with **N** Attribute Values:
  */

  /* - `IN` - true if a value is equal to any of the values in an enumerated list.
        Only scalar values are supported in the list, not sets. The target attribute
        must be of the same type and exact value in order to match */
  case class IN[A <: AnyAttribute](
    val attribute: A,
    val values: List[A#Raw]
  )(implicit
    ev: A#Raw isOneOf NotSetValues
  ) extends AnyCondition { type Attribute = A }


  /* ## Method aliases for condition constructors */
  case class ConditionAnyOps[A <: AnyAttribute](attribute: A) {
    final def isThere  = NOT_NULL(attribute)
    final def notThere =     NULL(attribute)

    final def ===(value: A#Raw): EQ[A] = EQ(attribute, value)
    final def  eq(value: A#Raw): EQ[A] = EQ(attribute, value)
  }

  case class ConditionWithPrefixOps[A <: AnyAttribute](attribute: A)
      (implicit ev: A#Raw isOneOf ValuesWithPrefixes) {

    final def beginsWith(value: A#Raw): BEGINS_WITH[A] = BEGINS_WITH(attribute, value)
  }

  case class ConditionNotSetOps[A <: AnyAttribute](attribute: A)
      (implicit ev: A#Raw isOneOf NotSetValues) {

    final def <(value: A#Raw): LT[A] = LT(attribute, value)
    final def ≤(value: A#Raw): LE[A] = LE(attribute, value)
    final def >(value: A#Raw): GT[A] = GT(attribute, value)
    final def ≥(value: A#Raw): GE[A] = GE(attribute, value)

    // non-symbolic names:
    final def lt(value: A#Raw): LT[A] = LT(attribute, value)
    final def le(value: A#Raw): LE[A] = LE(attribute, value)
    final def gt(value: A#Raw): GT[A] = GT(attribute, value)
    final def ge(value: A#Raw): GE[A] = GE(attribute, value)

    final def    between(start: A#Raw, end: A#Raw):     BETWEEN[A] =     BETWEEN(attribute, start, end)
    // final def notBetween(start: A#Raw, end: A#Raw): NOT_BETWEEN[A] = NOT_BETWEEN(attribute, start, end)

    final def isOneOf(values: List[A#Raw]): IN[A] = IN(attribute, values)
    final def      in(values: List[A#Raw]): IN[A] = IN(attribute, values)
  }

  case class ConditionSetOps[A <: AnyAttribute](attribute: A)
      (implicit ev: A#Raw isOneOf SetValues) {

    final def ∋[V](value: V)(implicit eq: Set[V] =:= A#Raw):     CONTAINS[A, V] =     CONTAINS(attribute, value)
    final def ∌[V](value: V)(implicit eq: Set[V] =:= A#Raw): NOT_CONTAINS[A, V] = NOT_CONTAINS(attribute, value)
  }

}
