
```scala
package ohnosequences.tabula

// TODO move to a different namespace
trait AwsService
object DynamoDB extends AwsService

trait AnyDynamoDBService { thisService =>
  
  // TODO move this to the type
  type Region <: AnyRegion
  type Account <: AnyAccount
  // type Auth <: AnyAuth
  val region: Region
  val account: Account
  // val auth: Auth

  val host = "amazonaws.com"
  val namespace: String = "dynamodb"
  // add here isSecure or something similar
  def endpoint: String

  // then you can do: service please createTable(table, initialState)
  // it could also be apply, like: service createTable(table, initialState)
  // TODO move to actionOps or something like that
  def please[A <: AnyAction](action: A)(implicit
    exec: Execute.For[A]
  ): exec.Out = exec()

  def apply[A <: AnyAction](action: A)(implicit
    exec: Execute.For[A]
  ): exec.Out = exec()

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
        + [conditions.scala][main/scala/tabula/conditions.scala]

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
[main/scala/tabula/conditions.scala]: conditions.scala.md