package ohnosequences.tabula.impl

import ohnosequences.tabula._
import ohnosequences.tabula.InitialState
import ohnosequences.tabula.Deleting
import ohnosequences.tabula.Creating
import com.amazonaws.services.dynamodbv2.model.{ProvisionedThroughput, CreateTableRequest, KeySchemaElement, AttributeDefinition}

trait DeleteTableAux extends AnyAction {
  override type Input <: AnyTable with Singleton
  //table
  override type Output = Input
  override type InputState = AnyTableState.For[Input]
  override type OutputState = Deleting[Output]
}

class DeleteTable[T <: AnyTable with Singleton](table: T, val state: AnyTableState.For[T]) extends DeleteTableAux {
  override val input: Input = table
  override type Input = T
}

object DeleteTable {

  implicit class DeleteTableExecute[D <: DeleteTableAux](ac: D)(implicit dynamoClient: DynamoDBClient) extends Execute {

    override type Action = D
    override val action = ac


    override def apply(): (action.Output, Deleting[action.Input]) = {
      println("executing: " + action)


      try {
        dynamoClient.client.deleteTable(action.input.name)
      } catch {
        case t: Throwable => t.printStackTrace()
      }

      (action.input, action.state.deleting)
    }

    override type C[+X] = X

  }

}


trait CreateHashKeyTableAux extends AnyAction {
  type HashKey <: AnyAttribute
  type Region <: AnyRegion

  override type Input <: HashKeyTable[HashKey, Region] with Singleton
  override type Output = Input

  //table
  override type InputState = InitialState[Input]
  override type OutputState = Creating[Input]
}

class CreateHashKeyTable[HK <: AnyAttribute, R <: AnyRegion, T <: HashKeyTable[HK, R] with Singleton](table: T, val state: InitialState[T]) extends CreateHashKeyTableAux {
  override val input: Input = table
  override type Input = T

  override type Region = R
  override type HashKey = HK
}

object CreateHashKeyTable {

  implicit class CreateTableExecute[A <: CreateHashKeyTableAux](ac: A)(implicit dynamoClient: DynamoDBClient, getAttributeDefinition: A#HashKey => AttributeDefinition) extends Execute {

    override type Action = A
    override val action = ac


    override def apply(): (action.Output, action.OutputState) = {

      val table = ac.input
      println("executing: " + action)
      // println(getHashDefinition(ac.input.hashKey))

      val attributeDefinition = getAttributeDefinition(ac.input.hashKey)
      val keySchemaElement = new KeySchemaElement(ac.input.hashKey.label, "HASH")
      val throughput = new ProvisionedThroughput(ac.state.initialThroughput.readCapacity, ac.state.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(keySchemaElement)
        .withAttributeDefinitions(attributeDefinition)

      try {
        dynamoClient.client.createTable(request)
      } catch {
        case t: Throwable => t.printStackTrace()
      }



      (action.input, action.state.creating)
    }

    override type C[+X] = X

  }

}

trait GetTableAux extends AnyAction {

  override type Input <: AnyTable with Singleton
  //table
  override type Output = Input

  override type InputState = AnyTableState.For[Input]
  override type OutputState = AnyTableState.For[Input]
}

class GetTable[T <: AnyTable with Singleton](table: T, val state: AnyTableState.For[T]) extends GetTableAux {
  override val input: Input = table
  override type Input = T
}

object GetTable {

  implicit class GetTableExecute[A <: GetTableAux](ac: A)(implicit dynamoClient: DynamoDBClient) extends Execute {

    override type Action = A
    override val action = ac


    override def apply(): (action.Output, AnyTableState.For[action.Input]) = {
      println("executing: " + action)
      val table = ac.input
      //CREATING, UPDATING, DELETING, ACTIVE
      dynamoClient.client.describeTable(table.name).getTable.getTableStatus match {
        case "ACTIVE" => Active(table, ac.state.account, ac.state.throughputStatus)
        case "CREATING" => Creating(table, ac.state.account, ac.state.throughputStatus)
        case "DELETING" => Deleting(table, ac.state.account, ac.state.throughputStatus)
        case "UPDATING" => Updating(table, ac.state.account, ac.state.throughputStatus)
      }

      (action.input, action.state.deleting)
    }

    override type C[+X] = X

  }


}
