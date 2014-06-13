
```scala
package ohnosequences.tabula

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

  import AnyDynamoDBService._

  // then you can do: service please createTable(table, initialState)
  // it could also be apply, like: service createTable(table, initialState)
  // TODO move to actionOps or something like that
  def please[A <: AnyAction.Of[thisService.type]](action: A)(implicit
    exec: Execute.For[A]
  ): exec.Out = exec()

  def apply[A <: AnyAction.Of[thisService.type]](action: A)(implicit
    exec: Execute.For[A]
  ): exec.Out = exec()


  trait AnyCreateTable extends AnyAction {

    type Service = thisService.type
    val service = thisService: thisService.type

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
```


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
  


```scala
  trait AnyGetItem extends AnyAction {

    type Input <: AnyItemType
  }
```


### Query

- [API - Query](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Query.html)

We need as input
  
- a hash key value
- _optional_ a condition on the range key
- the item type over which we want to query
- _optional_ a predicate over it for filtering results service-side


```scala
  trait AnyQuery extends AnyAction {}

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

  object AnyAction {

    type Of[S <: AnyDynamoDBService] = AnyAction { type Service = S }
  }

  trait Execute {

    type Action <: AnyAction
    val action: Action

    type C[+X]

    def apply(): Out

    type Out = C[(action.Output, action.OutputState)]
  }

  object Execute {
    
    type For[A <: AnyAction] = Execute { type Action = A }
  }

}

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]

[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula/attributes.scala]: attributes.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/conditions.scala]: conditions.scala.md