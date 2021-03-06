
```scala
package ohnosequences.tabula

import ohnosequences.scarph._

trait AnyDynamoDBResourceType {

  val name: String
}

object Table extends AnyDynamoDBResourceType {

  val name = "table"
}

trait AnyDynamoDBResource {

  type ResourceType <: AnyDynamoDBResourceType
  val  resourceType: ResourceType

  type Region <: AnyRegion
  val  region: Region

  val name: String
}

object AnyDynamoDBResource {
  type inRegion[R <: AnyRegion] = AnyDynamoDBResource { type Region = R }
}
```


see http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/UsingIAMWithDDB.html#ARN_ToPropertiesat


```scala
case class DynamoDBARN[R <: AnyDynamoDBResource](resource: R, account: Account) {

  type Resource = R

  // the AWS rep

  //println("resource.region.name: " + resource.region)
 // println("account.id: " + account.id)
 // println("resource.resourceType.name: " + resource.resourceType.name)
 // println("resource.name: " + resource.name)

  val id: String = s"arn:aws:dynamodb:${resource.region.name}:${account.id}:${resource.resourceType.name}/${resource.name}"
 // val id = "id"
}

sealed trait ResourceList {

  type Head <: AnyDynamoDBResource
  val head: Head

  type Tail <: ResourceList
  val tail: Tail
}

case class :+:[+H <: AnyDynamoDBResource, +T <: ResourceList](
  val h: H,
  val t: T
) extends ResourceList {

  type Head = h.type
  val  head = h: h.type
  type Tail = t.type
  val  tail = t: t.type
}

sealed trait RNil extends ResourceList {

  def :+:[H <: Singleton with AnyDynamoDBResource](h: H): (H :+: RNil) = ohnosequences.tabula.:+:(h, this) 
}
object RNil extends RNil with AnyDynamoDBResource {

  type Head = RNil.type
  val  head = RNil
  type Tail = RNil.type
  val  tail = RNil

  val name: String = ???
  val region: ohnosequences.tabula.RNil.Region = ???
  val resourceType: ohnosequences.tabula.RNil.ResourceType = ???
}

object ResourceList {

  implicit def toOps[RL <: ResourceList](rl: RL) = ResourceListOps(rl)
}

case class ResourceListOps[RL <: ResourceList](val rl: RL) {

  def :+:[H <: Singleton with AnyDynamoDBResource](h: H) : H :+: RL = ohnosequences.tabula.:+:(h, rl)
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