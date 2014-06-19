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
  val resourceType: ResourceType

  type Region <: AnyRegion
  val region: Region

  val name: String
}

object AnyDynamoDBResource {
  type inRegion[R <: AnyRegion] = AnyDynamoDBResource { type Region = R }
}

/*
  see http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/UsingIAMWithDDB.html#ARN_Format
*/
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

// TODO experiment with treating states as denotations of resources
trait AnyDynamoDBState { state =>

  type Resource <: AnyDynamoDBResource
  val resource: Resource

  val account: Account

  lazy val arn: DynamoDBARN[Resource] = DynamoDBARN(resource, account)
}

object AnyDynamoDBState {

  type Of[R <: AnyDynamoDBResource] = AnyDynamoDBState { type Resource = R }
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
  val head = h: h.type
  type Tail = t.type
  val tail = t: t.type
}

sealed trait RNil extends ResourceList {

  def :+:[H <: Singleton with AnyDynamoDBResource](h: H): (H :+: RNil) = ohnosequences.tabula.:+:(h, this) 
}
object RNil extends RNil with AnyDynamoDBResource {

  type Head = RNil.type
  val head = RNil
  type Tail = RNil.type
  val tail = RNil

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



