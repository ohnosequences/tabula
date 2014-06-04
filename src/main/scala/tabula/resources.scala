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

trait AnyDynamoDBStateType {

  type ResourceType <: AnyDynamoDBResourceType
  val resourceType: ResourceType
}

object AnyDynamoDBStateType {

  type Of[R <: AnyDynamoDBResourceType] = AnyDynamoDBStateType { type ResourceType = R }
}

trait AnyDynamoDBState extends Denotation[AnyDynamoDBStateType] {  

  type Resource <: AnyDynamoDBResource
  val resource: Resource
}

object AnyDynamoDBState {

  type Of[R <: AnyDynamoDBResource] = AnyDynamoDBState { type Resource = R }
}