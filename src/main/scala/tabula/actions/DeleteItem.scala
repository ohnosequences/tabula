package ohnosequences.tabula

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import ohnosequences.pointless.AnyTaggedType._

sealed trait DeleteItemResult
case object DeleteItemFail extends DeleteItemResult
case object DeleteItemSuccess extends DeleteItemResult

/* ### Common action trait */
trait AnyDeleteItemAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Output = DeleteItemResult
}


/* ### Hash key table */
trait AnyDeleteItemHashKeyAction extends AnyDeleteItemAction {

  type Table <: AnyHashKeyTable
  type Input = RawOf[Table#HashKey]
}

case class DeleteItemFromHashKeyTable[T <: AnyHashKeyTable]
(
  val t: T,
  val inputSt: AnyTableState.For[T] with ReadyTable
) 
{

  case class withKey(hashKeyValue: RawOf[T#HashKey]) extends AnyDeleteItemHashKeyAction {

    type Table = T
    val  table = t

    val input = hashKeyValue

    val inputState = inputSt

    override def toString = s"DeleteItemFromHashKeyTable ${t.name} withKey ${hashKeyValue}"
  }

}


/* ### Composite key table */
trait AnyDeleteItemCompositeKeyAction extends AnyDeleteItemAction {
  
  type Table <: AnyCompositeKeyTable
  type Input = ( RawOf[Table#HashKey], RawOf[Table#RangeKey] )
}

case class DeleteItemFromCompositeKeyTable[T <: AnyCompositeKeyTable]
  (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

  case class withKeys(
    val hashKeyValue: RawOf[T#HashKey],
    val rangeKeyValue: RawOf[T#RangeKey]
  ) 
  extends AnyDeleteItemCompositeKeyAction {

    type Table = T
    val  table = t

    val input = (hashKeyValue, rangeKeyValue)

    val inputState = inputSt

    override def toString = s"DeleteItemFromCompositeKeyTable ${t.name} withKeys ${(hashKeyValue, rangeKeyValue)}"
  }

}
