
```scala
package ohnosequences.tabula

import ohnosequences.scarph._
```


This is more or less OK by now.


```scala
trait AnyPredicate {

  type ItemType <: Singleton with AnyItemType
  val itemType: ItemType
}

// TODO add And and Or variants.

trait AnyOrPredicate extends AnyPredicate {

  def or[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): OR[this.type, Other] = OR[this.type, Other](this, other)

}
trait AnyAndPredicate extends AnyPredicate {

  def and[Other <: Condition](other: Other)(implicit 
    ev: ItemType HasProperty other.Attribute
  ): AND[this.type, Other] = AND[this.type, Other](this, other)
}

case class SimplePredicate[I <: Singleton with AnyItemType, C <: Condition](val itemType: I, val condition: C) 
  extends AnyPredicate with AnyOrPredicate with AnyAndPredicate {

  type ItemType = I
}

case class AND[P <: AnyAndPredicate, C <: Condition](val allThis: P, val also: C) 
  extends AnyAndPredicate { 

  type ItemType = P#ItemType
  val itemType = allThis.itemType 
}

case class OR[P <: AnyOrPredicate, C <: Condition](val allThis: P, val also: C) 
  extends AnyOrPredicate { 

  type ItemType = P#ItemType
  val itemType = allThis.itemType
} 


object AnyPredicate {

  type On[I <: AnyItemType] = AnyPredicate { type ItemType = I }

  // itemType ? condition == SimplePredicate(itemType, condition)
  implicit def toItemPredicateOps[I <: Singleton with AnyItemType](itemType: I): ItemPredicateOps[I] = ItemPredicateOps(itemType)
  case class ItemPredicateOps[I <: Singleton with AnyItemType](itemType: I) {
    def ?[C <: Condition](c: C)(implicit 
        ev: I HasProperty C#Attribute
      ): SimplePredicate[I, C] =
         SimplePredicate(itemType, c)
  }
}

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [queries.scala][main/scala/tabula/queries.scala]

[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula/attributes.scala]: attributes.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/queries.scala]: queries.scala.md