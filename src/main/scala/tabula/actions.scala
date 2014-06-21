package ohnosequences.tabula

import com.amazonaws.services.dynamodbv2.model.AttributeValue
// import ohnosequences.scarph.HasProperty
import ohnosequences.scarph._


trait AnyAction { action =>
  // this should be an HList of Resources; it is hard to express though
  type Resources
  val  resources: Resources

  type InputState
  val  inputState: InputState

  type OutputState

  // these are input and output that are not resources
  type Input
  val  input: Input

  type Output

  // trait Executor {
  //   val ac = action
  //   type OutC[+X]
  //   type Out = OutC[(ac.Output, ac.Resources, ac.OutputState)]

  //   def apply(): Out
  // }

  // trait IdExecutor extends action.Executor { type OutC[+X] = X }
}

object AnyAction {
  // TODO: this won't work with ResourcesList
  type inRegion[R <: AnyRegion] = AnyAction { type Resources <: AnyDynamoDBResource.inRegion[R] }
}

trait AnyTableAction extends AnyAction {
  type Table <: Singleton with AnyTable
  val  table: Table

  // TODO: change this to ResourcesList
  type Resources = Table //:+: RNil
  val  resources = table
}

object AnyTableAction {
  type withHashKeyTable      = AnyTableAction { type Table <: Singleton with AnyHashKeyTable }
  type withCompositeKeyTable = AnyTableAction { type Table <: Singleton with AnyCompositeKeyTable }
}

// actions

trait AnyCreateTable extends AnyTableAction {
  type InputState = InitialState[Table]
  type OutputState = Creating[Table]

  type Input = None.type
  val  input = None
  type Output = None.type
}

case class CreateTable[T <: Singleton with AnyTable](table: T, inputState: InitialState[T]) 
  extends AnyCreateTable { type Table = T }


trait AnyDeleteTable extends AnyTableAction {
  type InputState = Active[Table]
  type OutputState = Deleting[Table]

  type Input = None.type
  val  input = None
  type Output = None.type
}

case class DeleteTable[T <: Singleton with AnyTable](table: T, inputState: Active[T])
  extends AnyDeleteTable { type Table = T }


trait AnyDescribeTable extends AnyTableAction {
  type InputState  = AnyTableState.For[Table]
  type OutputState = AnyTableState.For[Table]

  type Input = None.type
  val  input = None
  type Output = None.type
}

case class DescribeTable[T <: Singleton with AnyTable](table: T, inputState: AnyTableState.For[T]) 
  extends AnyDescribeTable { type Table = T }


// TODO
trait AnyUpdateTable extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = Updating[Table]

  type Input = (Int, Int)
  val newReadThroughput: Int
  val newWriteThroughput: Int
  val input = (newReadThroughput, newWriteThroughput)

  type Output = None.type
}

// TODO
case class UpdateTable[T <: Singleton with AnyTable](
    table: T, 
    inputState: AnyTableState.For[T] with ReadyTable, 
    newReadThroughput: Int, 
    newWriteThroughput: Int
  ) extends AnyUpdateTable {
    type Table = T 
  }


trait AnyDeleteItemHashKey extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = Table#HashKey#Raw
  val  hashKeyValue: Input
  val  input = hashKeyValue

  type Output = None.type
}

case class DeleteItemHashKey[
    T <: AnyHashKeyTable with Singleton, 
    H <: T#HashKey#Raw
  ](table: T, 
    inputState: AnyTableState.For[T] with ReadyTable, 
    hashKeyValue: H
  ) extends AnyDeleteItemHashKey { type Table = T }


// TODO
trait AnyDeleteItemCompositeKey extends AnyTableAction {
  type Table <: Singleton with AnyCompositeKeyTable

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Input = (Table#HashKey#Raw, Table#RangeKey#Raw)
  val hashKeyValue: Table#HashKey#Raw
  val rangeKeyValue: Table#RangeKey#Raw
  val  input = (hashKeyValue, rangeKeyValue)

  type Output = None.type
}

// TODO
case class DeleteItemCompositeKey[
    T <: AnyCompositeKeyTable with Singleton, 
    RH <: T#HashKey#Raw, 
    RR <: T#RangeKey#Raw
  ](table: T, 
    inputState: AnyTableState.For[T] with ReadyTable, 
    hashKeyValue: RH, 
    rangeKeyValue: RR
  ) extends AnyDeleteItemCompositeKey { type Table = T }


sealed trait PutItemResult
case object PutItemFail extends PutItemResult
case object PutItemSuccess extends PutItemResult

//todo conditional part
// TODO
trait AnyPutItemHashKey extends AnyTableAction {
  type Table <: Singleton with AnyHashKeyTable

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem
  type ItemRep = Item#Rep

  type Input = (Item, ItemRep)
  type Output = PutItemResult
}


// TODO
case class PutItemHashKey[T <: AnyHashKeyTable with Singleton, I <: AnyItem with Singleton](
  table: T,
  inputState: AnyTableState.For[T] with ReadyTable,
  item: I,
  itemRep: I#Rep
)(implicit val hasHashKey: HasProperty[I, T#HashKey])
  extends AnyPutItemHashKey { override type Table = T; override type Item = I; val input = (item, itemRep)  }

trait AnyPutItemCompositeKey extends AnyTableAction {
  type Table <: Singleton with AnyCompositeKeyTable

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Input = item.Rep
  val input: Input
  type Output = PutItemResult

  val inputSDKRep: Map[String, AttributeValue]
}

case class InTable[T <: Singleton with AnyCompositeKeyTable](
    t: T, inputSt: AnyTableState.For[T] with ReadyTable
  ) {
  case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {
    case class withValue(itemRep: i.Rep)(implicit
      getSDKRep: i.Rep => Map[String, AttributeValue],
      hasHashKey:  i.type HasProperty t.HashKey,
      hasRangeKey: i.type HasProperty t.RangeKey
    ) extends AnyPutItemCompositeKey {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  inputSDKRep = getSDKRep(input)

      val inputState = inputSt
    }
  }
}



sealed trait GetItemResult { type Item <: AnyItem }
case class GetItemFail[I <: AnyItem]() extends GetItemResult { type Item = I }
case class GetItemSuccess[I <: Singleton with AnyItem](item: I#Raw) extends GetItemResult { type Item = I }


trait AnyGetItemCompositeKey extends AnyTableAction {
  type Table <: Singleton with AnyCompositeKeyTable

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Input = (table.hashKey.Raw, table.rangeKey.Raw)
  type Output = GetItemResult

  // def parseSDKItem(Map[String, AttributeValue]): item.Rep
  val parseSDKRep: Map[String, AttributeValue] => item.Rep
}

case class FromTable[T <: Singleton with AnyCompositeKeyTable](
    t: T, inputSt: AnyTableState.For[T] with ReadyTable
  ) {
  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {
    case class withKeys(
      hashKeyValue: t.hashKey.Raw,
      rangeKeyValue: t.rangeKey.Raw
    )(implicit
      hasHashKey:  i.type HasProperty t.HashKey,
      hasRangeKey: i.type HasProperty t.RangeKey,
      parse: Map[String, AttributeValue] => i.Rep
    ) extends AnyGetItemCompositeKey {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = inputSt

      val parseSDKRep = parse
    }
  }
}
/*
  #### GetItem

  - [API - GetItem](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_GetItem.html)

  This action depends on the table type, and thus its signature and implementation will be different for each. In the case of a HashKeyTable we need as **input**
  
  - an `item` object of type `Item` (**not** `ItemType`)
  - a value of type `table.hashKey.Rep`
  - _optional_ consistent read, capacity

  As per the output, we should get

  - the corresponding `item.Rep` value
  - possibly errors instead

  ##### input, inputState

  In principle, we should have something like

  - `input` correspond to the table from which you want to read the item
  - `inputState` being the key value, the `item` and whatever else is needed

  This sounds like more orthodox in principle, but it could be confusing _if_ the action class mirrors this in its parameters: `service getItem(table, otherStuff(key, item))`. But this does not need to be so: just use the table inside `item` to set the input, and use a more intuitive set of parameters: `service getItem(item, key)`. Actually, as a table is the only resource in DynamoDB, for all DynamoDB actions the input is going to be formed by tables.

*/
// trait AnyGetItem extends AnyTableAction {

//   type Input <: AnyItemType
// }

/*
  ### Query

  - [API - Query](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Query.html)

  We need as input

  - a hash key value
  - _optional_ a condition on the range key
  - the item type over which we want to query
  - _optional_ a predicate over it for filtering results service-side
*/
trait AnyQuery extends AnyTableAction {}
