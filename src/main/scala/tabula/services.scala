package ohnosequences.tabula

// TODO move to a different namespace
trait AwsService
object DynamoDB extends AwsService

trait AnyDynamoDBService { self =>
  
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

  import AnyDynamoDBService._

  // then you can do: service please createTable(table, initialState)
  // it could also be apply, like: service createTable(table, initialState)
  // TODO move to actionOps or something like that
  def please[A <: AnyAction { type Service = self.type }](action: A)(implicit
    exec: Execute { type Action = A }
  ): exec.Out[(A#Output, A#OutputState)] = exec()

  trait AnyCreateTable extends AnyAction {

    type Service = self.type
    val service = self: self.type

    type Input <: Singleton with AnyTable
    type InputState = InitialState[Input]

    type Output = Input
    // TODO this should be something concrete
    type OutputState = AnyTableState { type Resource = Input }
  }

  case class CreateTable[T <: Singleton with AnyTable](val input: T, val state: InitialState[T])
  extends AnyCreateTable {

    type Input = T
  }

  trait AnyDeleteTable extends AnyAction {

    type Input <: Singleton with AnyTable
    type InputState <: AnyTableState { type Resource = Input }

    type Output = Input
    // TODO this should be something concrete
    type OutputState = AnyTableState { type Resource = Input }
  }

  trait AnyGetItem {

    type Input <: AnyItemType
    // type InputState = input.
  }

}

object AnyDynamoDBService {

  trait AnyAction {

    type Service <: AnyDynamoDBService
    val service: Service

    // this should be an HList of Resources; it is hard to express though
    type Input
    val input: Input
    // same for this
    type Output

    type InputState
    val state: InputState
    type OutputState

    // def apply(): (Output, OutputState) 
  }

  trait Execute {

    type Action <: AnyAction
    val action: Action

    type Out[+X]

    def apply(): Out[(action.Output, action.OutputState)]
  }

}
