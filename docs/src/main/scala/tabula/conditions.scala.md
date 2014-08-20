
```scala
package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
```


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


```scala
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
```


### Comparison Operators with **No** Property Values


```scala
sealed trait NullaryCondition[A <: Singleton with AnyProperty] 
  extends Condition { type Property = A }
```

- `NOT_NULL` - true if an property exists

```scala
case class     NULL[A <: Singleton with AnyProperty](val property: A) extends NullaryCondition[A]
```

- `NULL` - true if an property does not exist

```scala
case class NOT_NULL[A <: Singleton with AnyProperty](val property: A) extends NullaryCondition[A]
```


## Comparison Operators with **One** Property Value


```scala
trait SimpleCondition[A <: Singleton with AnyProperty] extends Condition {

  type Property = A
  val value: A#Raw
}
```

- `EQ` - true if an property is equal to a value

```scala
case class EQ[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
) extends SimpleCondition[A] with KeyCondition
```

- `NE` - true if an property is not equal to a value

```scala
// NOTE: this is not a KeyCondition for some reason
case class NE[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
) extends SimpleCondition[A]
```

- `LE` - true if an property is less than or equal to a value

```scala
case class LE[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition
```

- `LT` - true if an property is less than a value

```scala
case class LT[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit ev: A#Raw :<: NotSetValues)
  extends SimpleCondition[A] with KeyCondition
```

- `GE` - true if an property is greater than or equal to a value

```scala
case class GE[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition
```

- `GT` - true if an property is greater than a value

```scala
case class GT[A <: Singleton with AnyProperty](
  val property: A,
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: NotSetValues
) extends SimpleCondition[A] with KeyCondition
```

- `CONTAINS` - true if a value is present within a set, or if one value contains another

```scala
// NOTE: the value here is a set!
case class CONTAINS[A <: Singleton with AnyProperty, V](
  val property: A, 
  val value: V
)(implicit 
  ev: A#Raw :<: SetValues,
  eq: Set[V] =:= A#Raw
) extends Condition { type Property = A }
```

- `NOT_CONTAINS` - true if a value is not present within a set, or if one value does not contain another value

```scala
case class NOT_CONTAINS[A <: Singleton with AnyProperty, V](
  val property: A, 
  val value: V
)(implicit 
  ev: A#Raw :<: SetValues,
  eq: Set[V] =:= A#Raw
) extends Condition { type Property = A }
```

- `BEGINS_WITH` - true if the first few characters of an property match the provided value. Do not use this operator for comparing numbers

```scala
case class BEGINS_WITH[A <: Singleton with AnyProperty](
  val property: A, 
  val value: A#Raw
)(implicit 
  ev: A#Raw :<: ValuesWithPrefixes
) extends SimpleCondition[A] with KeyCondition
```


## Comparison Operators with **Two** Property Values

- `BETWEEN` - true if a value is between a lower bound and an upper bound, endpoints inclusive

```scala
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

```


## Comparison Operators with **N** Property Values:

- `IN` - true if a value is equal to any of the values in an enumerated list. 
   Only scalar values are supported in the list, not sets. The target property 
   must be of the same type and exact value in order to match

```scala
case class IN[A <: Singleton with AnyProperty](
  val property: A,
  val values: List[A#Raw]
)(implicit
  ev: A#Raw :<: NotSetValues
) extends Condition { type Property = A }
```

## Method aliases for condition constructors

```scala
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
  final def ?(value: A#Raw): LE[A] = LE(property, value)
  final def >(value: A#Raw): GT[A] = GT(property, value)
  final def ?(value: A#Raw): GE[A] = GE(property, value)

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

  final def ?[V](value: V)(implicit eq: Set[V] =:= A#Raw):     CONTAINS[A, V] =     CONTAINS(property, value)
  final def ?[V](value: V)(implicit eq: Set[V] =:= A#Raw): NOT_CONTAINS[A, V] = NOT_CONTAINS(property, value)
}

```


------

### Index

+ src
  + main
    + scala
      + tabula
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
          + [Query.scala][main/scala/tabula/actions/Query.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + impl
          + actions
            + [GetItem.scala][main/scala/tabula/impl/actions/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/actions/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/actions/Query.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/executors/Query.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
          + [ImplicitConversions.scala][main/scala/tabula/impl/ImplicitConversions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
      + [tabula.scala][main/scala/tabula.scala]
  + test
    + scala
      + tabula
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
        + [items.scala][test/scala/tabula/items.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]

[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/conditions.scala]: conditions.scala.md
[main/scala/tabula/executors.scala]: executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: impl/actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: impl/actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: impl/actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: impl/Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: impl/executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: impl/executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: impl/ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/states.scala]: states.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../test/scala/tabula/impl/irishService.scala.md
[test/scala/tabula/items.scala]: ../../../test/scala/tabula/items.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md