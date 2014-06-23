
```scala
package ohnosequences.tabula

import ohnosequences.typesets._
```


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


```scala
trait Condition {
  
  type Attribute <: Singleton with AnyAttribute
  val  attribute: Attribute
}

trait KeyCondition extends Condition

object Condition {
  implicit def conditionNotSetOps[A <: Singleton with AnyAttribute](attribute: A)
    (implicit ev: A#Raw :<: NotSetValues): 
      ConditionNotSetOps[A] = 
      ConditionNotSetOps[A](attribute)

  implicit def conditionSetOps[A <: Singleton with AnyAttribute](attribute: A)
    (implicit ev: A#Raw :<: SetValues): 
      ConditionSetOps[A] = 
      ConditionSetOps[A](attribute)

  // implicit def toSet[A <: Singleton with AnyAttribute, V](v: V)(implicit eq: Set[V] =:= A#Raw): A#Raw = eq(Set[V](v))
}
```


### Comparison Operators with **No** Attribute Values


```scala
sealed trait NullaryCondition[A <: Singleton with AnyAttribute] 
  extends Condition { type Attribute = A }
```

- `NOT_NULL` - true if an attribute exists

```scala
case class     NULL[A <: Singleton with AnyAttribute](val attribute: A) extends NullaryCondition[A]
```

- `NULL` - true if an attribute does not exist

```scala
case class NOT_NULL[A <: Singleton with AnyAttribute](val attribute: A) extends NullaryCondition[A]
```


## Comparison Operators with **One** Attribute Value


```scala
trait SimpleCondition[A <: Singleton with AnyAttribute] extends Condition {

  type Attribute = A
  val value: A#Raw
}
```

- `EQ` - true if an attribute is equal to a value

```scala
case class EQ[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
) extends SimpleCondition[A] with KeyCondition
```

- `NE` - true if an attribute is not equal to a value

```scala
// NOTE: this is not a KeyCondition for some reason
case class NE[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
) extends SimpleCondition[A]
```

- `LE` - true if an attribute is less than or equal to a value

```scala
case class LE[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition
```

- `LT` - true if an attribute is less than a value

```scala
case class LT[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit ev: A#Raw :<: NotSetValues)
  extends SimpleCondition[A] with KeyCondition
```

- `GE` - true if an attribute is greater than or equal to a value

```scala
case class GE[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition
```

- `GT` - true if an attribute is greater than a value

```scala
case class GT[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition
```

- `CONTAINS` - true if a value is present within a set, or if one value contains another

```scala
// NOTE: the value here is a set!
case class CONTAINS[A <: Singleton with AnyAttribute, V](
  val attribute: A, 
  val value: V
)(implicit 
  ev: A#Raw :<: SetValues,
  eq: Set[V] =:= A#Raw
) extends Condition { type Attribute = A }
```

- `NOT_CONTAINS` - true if a value is not present within a set, or if one value does not contain another value

```scala
case class NOT_CONTAINS[A <: Singleton with AnyAttribute, V](
  val attribute: A, 
  val value: V
)(implicit 
  ev: A#Raw :<: SetValues,
  eq: Set[V] =:= A#Raw
) extends Condition { type Attribute = A }
```

- `BEGINS_WITH` - true if the first few characters of an attribute match the provided value. Do not use this operator for comparing numbers

```scala
case class BEGINS_WITH[A <: Singleton with AnyAttribute](
  val attribute: A, 
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: ValuesWithPrefixes
) extends SimpleCondition[A] with KeyCondition
```


## Comparison Operators with **Two** Attribute Values

- `BETWEEN` - true if a value is between a lower bound and an upper bound, endpoints inclusive

```scala
case class BETWEEN[A <: Singleton with AnyAttribute](
  val attribute: A,
  val start: A#Raw,
  val end: A#Raw
)(implicit
  ev: A#Raw :<: NotSetValues
) extends KeyCondition { type Attribute = A }

// NOTE: this is not in the Amazon documentation
case class NOT_BETWEEN[A <: Singleton with AnyAttribute](
  val attribute: A,
  val start: A#Raw,
  val end: A#Raw
)(implicit
  ev: A#Raw :<: NotSetValues
) extends KeyCondition { type Attribute = A }
```


## Comparison Operators with **N** Attribute Values:

- `IN` - true if a value is equal to any of the values in an enumerated list. 
   Only scalar values are supported in the list, not sets. The target attribute 
   must be of the same type and exact value in order to match

```scala
case class IN[A <: Singleton with AnyAttribute](
  val attribute: A,
  val values: List[A#Raw]
)(implicit
  ev: A#Raw :<: NotSetValues
) extends Condition { type Attribute = A }
```

## Method aliases for condition constructors

```scala
class ConditionAnyOps[A <: Singleton with AnyAttribute](attribute: A) {
  final def isThere  = NOT_NULL(attribute)
  final def notThere =     NULL(attribute)
  final def ===(value: A#Raw): EQ[A] = EQ(attribute, value)
}

case class ConditionNotSetOps[A <: Singleton with AnyAttribute](attribute: A)
    (implicit ev: A#Raw :<: NotSetValues) extends ConditionAnyOps[A](attribute) {

  final def <(value: A#Raw): LT[A] = LT(attribute, value)
  final def ≤(value: A#Raw): LE[A] = LE(attribute, value)
  final def >(value: A#Raw): GT[A] = GT(attribute, value)
  final def ≥(value: A#Raw): GE[A] = GE(attribute, value)

  final def    between(start: A#Raw, end: A#Raw):     BETWEEN[A] =     BETWEEN(attribute, start, end)
  final def notBetween(start: A#Raw, end: A#Raw): NOT_BETWEEN[A] = NOT_BETWEEN(attribute, start, end)

  final def isOneOf(values: List[A#Raw]): IN[A] = IN(attribute, values)
}

case class ConditionSetOps[A <: Singleton with AnyAttribute](attribute: A)
    (implicit ev: A#Raw :<: SetValues) extends ConditionAnyOps[A](attribute) {

  final def ∋[V](value: V)(implicit eq: Set[V] =:= A#Raw):     CONTAINS[A, V] =     CONTAINS(attribute, value)
  final def ∌[V](value: V)(implicit eq: Set[V] =:= A#Raw): NOT_CONTAINS[A, V] = NOT_CONTAINS(attribute, value)
}

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + impl
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
          + [AttributeImplicits.scala][main/scala/tabula/impl/AttributeImplicits.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]

[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../test/scala/tabula/impl/irishService.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: impl/Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: impl/executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: impl/AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/states.scala]: states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: executors.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula/attributes.scala]: attributes.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/conditions.scala]: conditions.scala.md