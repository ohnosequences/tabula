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

  trait AnyCreateTable extends Action {

    type Input <: AnyTable
    // should be refined
    type InputState <: AnyTableState { type Resource = Input }

    type Output = Input
    type OutputState <: AnyTableState { type Resource = Output }
  }

  abstract class CreateTable[
    I <: AnyTable,
    IT <: AnyTableState { type Resource = I }
  ](
    val table: I,
    val initialState: IT
  ) extends AnyCreateTable {

    type Input = I
    val input = table

    type InputState = IT
    val state = initialState
  }
}
