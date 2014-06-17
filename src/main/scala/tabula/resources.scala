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


trait AnyDynamoDBState { state =>

  type Resource <: AnyDynamoDBResource
  val resource: Resource

  val account: Account

  val arn: DynamoDBARN[Resource] = DynamoDBARN(resource, account)
}

object AnyDynamoDBState {

  type Of[R <: AnyDynamoDBResource] = AnyDynamoDBState { type Resource = R }
}
