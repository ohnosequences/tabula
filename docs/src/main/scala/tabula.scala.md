
```scala
package ohnosequences

import ohnosequences.typesets._

// in package object only type-aliases
package object tabula {
  
  type Bytes = Seq[Byte]
  type Num   = Int
  // not documented; the API informs you about it if you try not to adhere to it
  type NotSetValues = either[Num]#or[String]#or[Bytes]
  type PrimaryKeyValues = NotSetValues
  type ValidValues = NotSetValues#or[Set[Num]]#or[Set[String]]#or[Set[Bytes]]
  type ValuesWithPrefixes = either[String]#or[Bytes]
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

[test/scala/tabula/simpleModel.scala]: ../../test/scala/tabula/simpleModel.scala.md
[main/scala/tabula.scala]: tabula.scala.md
[main/scala/tabula/predicates.scala]: tabula/predicates.scala.md
[main/scala/tabula/accounts.scala]: tabula/accounts.scala.md
[main/scala/tabula/regions.scala]: tabula/regions.scala.md
[main/scala/tabula/items.scala]: tabula/items.scala.md
[main/scala/tabula/resources.scala]: tabula/resources.scala.md
[main/scala/tabula/actions.scala]: tabula/actions.scala.md
[main/scala/tabula/tables.scala]: tabula/tables.scala.md
[main/scala/tabula/attributes.scala]: tabula/attributes.scala.md
[main/scala/tabula/services.scala]: tabula/services.scala.md
[main/scala/tabula/queries.scala]: tabula/queries.scala.md