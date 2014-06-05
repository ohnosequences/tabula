package ohnosequences.tabula

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

    type Input <: Singleton with AnyTableType
    type InputState <: AnyTableState { type ResourceType = Input }

    type Output = table[Input]
    // TODO this should be something concrete
    type OutputState = AnyTableState { type ResourceType = Input }
  }

  abstract class CreateTable[
    T <: Singleton with AnyTableType,
    TS <: AnyTableState { type ResourceType = T }
  ](
    val input: T,
    val state: TS
  )
  extends AnyCreateTable {

    type Input = T
    type InputState = TS
  }

  case class createTable[
    T <: Singleton with AnyTableType,
    TS <: AnyTableState { type ResourceType = T }
  ](
    override val input: T,
    override val state: TS
  ) extends CreateTable(input,state) {

    def apply(): (table[Input], AnyTableState { type ResourceType = T }) = ???
  }

  case class table[T <: Singleton with AnyTableType](val tpe: T) extends AnyTable {

    type Tpe = T
    type Service = self.type
    val service = self:self.type

    // TODO actually do something with this
    type ARN = AnyDynamoDBARN
    val arn: ARN = new AnyDynamoDBARN {}
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
