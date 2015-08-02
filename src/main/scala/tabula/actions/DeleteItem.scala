package ohnosequences.tabula.action

// import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.cosas.types._
import ohnosequences.tabula._, items._, actions._, tables._, states._

// sealed trait DeleteItemResult
// case object DeleteItemFail extends DeleteItemResult
// case object DeleteItemSuccess extends DeleteItemResult

case class DeleteItem[I <: AnyItem](
  val item: I,
  val keyValue: I#Table#PrimaryKey#Raw
) extends AnyItemAction {

  type Item = I

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = None.type
}
