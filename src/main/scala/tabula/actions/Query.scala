package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait QueryResult { type Item <: AnyItem }
case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult { type Item = I }
case class QuerySuccess[I <: Singleton with AnyItem](item: List[I#Raw]) extends QueryResult { type Item = I }

/* ### Common action trait */
trait AnyQueryAction extends AnyTableItemAction { action =>
  // quieries make sense only for the composite key tables
  type Table <: Singleton with AnyCompositeKeyTable

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = QueryResult { type Item = action.Item }
}

trait AnySimpleQueryAction extends AnyQueryAction {
  type Input = table.hashKey.Raw
}

// the range key condition is optional
trait AnyNormalQueryAction extends AnySimpleQueryAction {
  type RangeCondition <: Condition.On[table.RangeKey] with KeyCondition
  val  rangeCondition: RangeCondition
}
