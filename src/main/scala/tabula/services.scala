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
  // def apply[A <: AnyAction, E <: Executor.For[A]](action: A)
  //   (implicit mkE: A => E): E#Out = {
  //   // E#OutC[(A#Output, A#Resources, A#OutputState)] = {
  //     val exec = mkE(action)
  //     exec()
  //   }

  def please[A <: AnyAction, E <: Executor.For[A]](action: A)
    (implicit exec: E): action.InputState => exec.OutC[ExecutorResult[action.Output, action.OutputState]] = {
      // val exec = mkE(action)
      s => exec(action)(s)
    }
}
