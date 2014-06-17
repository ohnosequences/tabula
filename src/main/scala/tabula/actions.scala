package ohnosequences.tabula

trait AnyAction {

  // this should be an HList of Resources; it is hard to express though
  type Input
  val input: Input
  // same for this
  type Output

  type InputState
  val state: InputState
  type OutputState
}

// actions

trait AnyCreateTable extends AnyAction {

  type Input <: Singleton with AnyTable
  type InputState = InitialState[Input]

  type Output = Input
  // TODO this should be something concrete
  type OutputState = AnyTableState { type Resource = Input }
}

case class CreateTable[T <: Singleton with AnyTable](val input: T, val state: InitialState[T]) extends AnyCreateTable {

  type Input = T
}

trait AnyDeleteTable extends AnyAction {

  type Input <: Singleton with AnyTable
  type InputState <: AnyTableState { type Resource = Input }

  type Output = Input
  // TODO this should be something concrete
  type OutputState <: AnyTableState { type Resource = Input }
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
trait AnyGetItem extends AnyAction {

  type Input <: AnyItemType
}

/*
  ### Query

  - [API - Query](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Query.html)

  We need as input

  - a hash key value
  - _optional_ a condition on the range key
  - the item type over which we want to query
  - _optional_ a predicate over it for filtering results service-side
*/
trait AnyQuery extends AnyAction {}


trait Execute {

  type Action <: AnyAction
  // val action: Action

  type C[+X]

  def apply(action: Action): Out

  type Out = C[(Action#Output ,Action#OutputState)]
}




object Execute {
  
  type For[A <: AnyAction] = Execute { type Action = A }
  type For2[A <: AnyAction, B[_]] = Execute { type Action = A; type  C[X] = B[X]  }
}

