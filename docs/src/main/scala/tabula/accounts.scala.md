
```scala
package ohnosequences.tabula

trait AnyAccount {

  val id: String
  val canonical_id: String
}

case class Account(val id: String, val canonical_id: String)
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