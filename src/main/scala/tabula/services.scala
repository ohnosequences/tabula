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
    exec: A => Execute.For[A]
  ): Execute.For[A]#Out = exec(action).apply()

  def apply[A <: AnyAction, B[+_]](action: A)(implicit
    exec: A => Execute.For2[A, B]
  ): B[(A#Output, A#OutputState)] = exec(action).apply()

}