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
  def please[A <: Action { type Service = self.type }](action: A): (action.Output, action.OutputState) = action()
  // then you can do: service please createTable(table, initialState)

  trait AnyCreateTable extends Action {

    type Service = self.type
    val service = self:self.type

    type Input <: Singleton with AnyTable
    type InputState = InitialState[Input]

    type Output = Input
    // TODO this should be something concrete
    type OutputState = AnyTableState { type Resource = Input }
  }

  abstract class CreateTable[
    T <: Singleton with AnyTable
  ](
    val input: T,
    val state: InitialState[T]
  )
  extends AnyCreateTable {

    type Input = T

    def apply(): (T, AnyTableState { type Resource = T }) = ???
  }

  trait AnyDeleteTable extends Action {

    type Input <: Singleton with AnyTable
    type InputState <: AnyTableState { type Resource = Input }

    type Output = Input
    // TODO this should be something concrete
    type OutputState = AnyTableState { type Resource = Input }
  }

}

object AnyDynamoDBService {

  trait Action {

    type Service <: AnyDynamoDBService
    val service: Service

    // this should be an HList of Resources
    type Input
    val input: Input
    // same for this
    type Output

    type InputState
    val state: InputState
    type OutputState

    def apply(): (Output, OutputState) 
  }

  
}
