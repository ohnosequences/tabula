
```scala
package ohnosequences.tabula

import ohnosequences.scarph._
```


## Predicates

Predicates represent complex expressions using multiple conditions on a particular item.
You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
Predicate constructors check that the item has the attribute used in the applied condition.


```scala
trait AnyPredicate {
  type Body <: AnyPredicate
  val  body: Body

  type Head <: Condition
  val  head: Head

  type Item <: Singleton with AnyItem
  val  item: Item
}
```

### OR Predicates

```scala
trait AnyOrPredicate extends AnyPredicate {

  type Body <: AnyOrPredicate

  def or[Head <: Condition](other: Head)(implicit 
    ev: body.Item HasProperty other.Attribute
  ): OR[Body, Head] = 
     OR(body, other)
}

case class OR[B <: AnyOrPredicate, H <: Condition]
  (val body : B,  val head : H) extends AnyOrPredicate {
  type Body = B; type Head = H

  type Item = body.Item
  val  item = body.item
}
```

### AND Predicates

```scala
trait AnyAndPredicate extends AnyPredicate {

  type Body <: AnyAndPredicate

  def and[Head <: Condition](other: Head)(implicit 
    ev: body.Item HasProperty other.Attribute
  ): AND[Body, Head] = 
     AND(body, other)
}

case class AND[B <: AnyAndPredicate, H <: Condition]
  (val body : B,  val head : H) extends AnyAndPredicate {
  type Body = B; type Head = H

  type Item = body.Item
  val  item = body.item 
}
```


### Initial Predicate 

It contains only one condition and can be extended the either to `OR` or `AND` predicate


```scala
trait AnySimplePredicate extends AnyOrPredicate with AnyAndPredicate {
  type Body = this.type
  val  body = this: this.type
}

case class SimplePredicate[I <: Singleton with AnyItem, C <: Condition]
  (val item : I,  val head : C) extends AnySimplePredicate {
  type Item = I; type Head = C
}


object AnyPredicate {

  type HeadedBy[C <: Condition] = AnyPredicate { type Head <: C }

  type On[I <: AnyItem] = AnyPredicate { type Item = I }
```


With this you can write `item ? condition` which means `SimplePredicate(item, condition)`


```scala
  implicit def itemPredicateOps[I <: Singleton with AnyItem](item: I): ItemPredicateOps[I] = ItemPredicateOps(item)
  case class   ItemPredicateOps[I <: Singleton with AnyItem](item: I) {
    def ?[C <: Condition](c: C)(implicit 
        ev: item.type HasProperty c.Attribute
      ): SimplePredicate[I, C] = SimplePredicate(item, c)
  }
}
```

An implicit check that this predicate consists only of KeyConditions

```scala
sealed class OnlyWitnKeyConditions[P <: AnyPredicate]

object OnlyWitnKeyConditions extends OnlyWitnKeyConditions2 {
  implicit def simple[P <: AnySimplePredicate with AnyPredicate.HeadedBy[KeyCondition]]:
    OnlyWitnKeyConditions[P] = new OnlyWitnKeyConditions[P]
}

// This needs to be separated, otherwise implicits will deverge for SimplePredicate
trait OnlyWitnKeyConditions2 {

  implicit def and[P <: AnyAndPredicate with AnyPredicate.HeadedBy[KeyCondition]]
    (implicit ev: OnlyWitnKeyConditions[P#Body]):
                  OnlyWitnKeyConditions[P] = 
              new OnlyWitnKeyConditions[P]

  implicit def  or[P <: AnyOrPredicate  with AnyPredicate.HeadedBy[KeyCondition]]
    (implicit ev: OnlyWitnKeyConditions[P#Body]):
                  OnlyWitnKeyConditions[P] = 
              new OnlyWitnKeyConditions[P]
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