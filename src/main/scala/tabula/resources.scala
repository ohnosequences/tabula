package ohnosequences.tabula

import ohnosequences.scarph._

trait AnyDynamoDBResourceType {}
// only tables in this case

trait AnyDynamoDBResource {

  type Service <: AnyDynamoDBService
  val service: Service

  type ARN <: AnyDynamoDBARN
  val arn: ARN

  // type Region <: AWSRegion
}
// instances of resources: a particular table etc

trait DynamoDBResource[X <: AnyDynamoDBResourceType] extends Denotation[X] with AnyDynamoDBResource {}

trait AnyDynamoDBARN {}

trait AnyDynamoDBState {

  type ResourceType <: AnyDynamoDBResourceType
  val resourceType: ResourceType
}

object AnyDynamoDBState {

  type Of[R <: AnyDynamoDBResourceType] = AnyDynamoDBState { type ResourceType = R }
}