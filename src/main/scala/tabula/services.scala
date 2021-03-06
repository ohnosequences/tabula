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

    // then you can do: service please createTable(table, initialState)
    // it could also be apply, like: service createTable(table, initialState)
    // def apply[A <: AnyAction, E <: Executor.For[A]](action: A)
    //   (implicit mkE: A => E): E#Out = {
    //   // E#OutC[(A#Output, A#Resources, A#OutputState)] = {
    //     val exec = mkE(action)
    //     exec()
    //   }

    // def plz[A <: AnyTableAction, E <: ExecutorFor[A]](action: A)
    //   (implicit exec: (A#Table, A) => E): A#InputState => OutOf[E] = { s => exec(action.table, action)(action)(s) }

    def please[A <: AnyAction, E <: ExecutorFor[A]](action: A)
      (implicit mkExec: A => E): A#InputState => E#Out = {
        val exec = mkExec(action)
        s => exec(action)(s)
      }

    // def please[A <: AnyAction, E <: ExecutorFor[A]](action: A)
    //   (implicit exec: E): A#InputState => OutOf[E] = { s => exec(action)(s) }
  }

}
