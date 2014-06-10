
```scala
package ohnosequences.tabula

import ohnosequences.scarph._
```


## Items

This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.

### items and keys

The primary key of an item is accessible through the corresponding table


```scala
trait AnyItemType {

  type Table <: AnyTable
  val table: Table
}


object AnyItemType {

  type of[T <: AnyTable] = AnyItemType { type Table = T }
  implicit def itemTypeOps[IT <: AnyItemType](itemType: IT) = ItemTypeOps(itemType)
}

class ItemType[T <: AnyTable](val table: T) extends AnyItemType {

  type Table = T
}

case class ItemTypeOps[IT <: AnyItemType](val itemType: IT) {

  def has[P <: AnyAttribute](p: P) = HasProperty[IT, P](itemType, p)
}
```


Items are denotations of an item type. the table is accessible through the item type.


```scala
trait AnyItem extends Denotation[AnyItemType] with HasProperties {}
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