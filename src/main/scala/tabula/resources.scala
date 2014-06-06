package ohnosequences.tabula

import ohnosequences.scarph._

trait AnyDynamoDBResource {

  type Region <: AnyRegion
  val region: Region  

  // type Region <: AWSRegion
}

trait AnyDynamoDBARN {

  type Resource <: AnyDynamoDBResource
  val resource: Resource

  // the AWS rep
  val id: String
}


trait AnyDynamoDBState { state =>

  type Resource <: AnyDynamoDBResource
  val resource: Resource

  val account: Account

  type ARN <: AnyDynamoDBARN { type Resource = state.Resource }
  val arn: ARN
}

object AnyDynamoDBState {

  type Of[R <: AnyDynamoDBResource] = AnyDynamoDBState { type Resource = R }
}