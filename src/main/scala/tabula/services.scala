package ohnosequences.tabula

case object services {

  import accounts._, regions._, actions._, executors._


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

    def execute[A <: AnyAction, E <: ExecutorFor[A]](action: A)
      (implicit exec: E): A#InputState => E#Out = {
        s => exec(action)(s)
      }

  }

}
