package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

object Executors {

  case class DeleteTableExecute[D <: AnyDeleteTable](dynamoClient: AnyDynamoDBClient) extends Executor {
    type Action = D

    override type C[+X] = X

    override def apply(action: D): Out = {
      println("executing: " + action)
      (action.input, action.state.deleting)
    }
  }

  implicit def deleteTableExecute[D <: AnyDeleteTable]
    (implicit dynamoClient: AnyDynamoDBClient): DeleteTableExecute[D] = DeleteTableExecute[D](dynamoClient)


  case class CreateHashKeyTableExecute[A <: AnyCreateTable.withHashKeyTable](
      dynamoClient: AnyDynamoDBClient,
      getAttributeDefinition: A#HashKey => AttributeDefinition
    ) extends Executor {

    override type Action = A

    override def apply(ac: A): Out = {

      val table = ac.input
      println("executing: " + ac)
     // println(getHashDefinition(ac.input.hashKey))

      val attributeDefinition = getAttributeDefinition(ac.input.hashKey)
      val keySchemaElement = new KeySchemaElement(ac.input.hashKey.label, "HASH")
      val throughput = new ProvisionedThroughput(ac.state.initialThroughput.readCapacity, ac.state.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(keySchemaElement)
        .withAttributeDefinitions(attributeDefinition)

      dynamoClient.client.createTable(request)

      (ac.input, ac.state.creating)
    }

    override type C[+X] = X
  }

  implicit def createHashKeyTableExecute[A <: AnyCreateTable.withHashKeyTable](implicit 
      dynamoClient: AnyDynamoDBClient,
      getAttributeDefinition: A#HashKey => AttributeDefinition
    ): CreateHashKeyTableExecute[A] =
       CreateHashKeyTableExecute[A](dynamoClient, getAttributeDefinition)


  case class DescribeTableExecute[A <: AnyDescribeTable](dynamoClient: AnyDynamoDBClient) extends Executor {

    override type Action = A

    override def apply(action: A): Out = {
      println("executing: " + action)
      val table = action.input
      //CREATING, UPDATING, DELETING, ACTIVE
      dynamoClient.client.describeTable(table.name).getTable.getTableStatus match {
        case "ACTIVE" => Active(table, action.state.account, action.state.throughputStatus)
        case "CREATING" => Creating(table, action.state.account, action.state.throughputStatus)
        case "DELETING" => Deleting(table, action.state.account, action.state.throughputStatus)
        case "UPDATING" => Updating(table, action.state.account, action.state.throughputStatus)
      }

      (action.input, action.state.deleting)
    }

    override type C[+X] = X

  }

  implicit def describeTableExecute[A <: AnyDescribeTable]
    (implicit dynamoClient: AnyDynamoDBClient): DescribeTableExecute[A] =
      DescribeTableExecute[A](dynamoClient)

}
