package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

object Executors {

  case class DeleteTableExecute[A <: AnyDeleteTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]) extends Executor {
    type Action = A

    override type C[+X] = X

    override def apply(action: A): Out = {
      println("executing: " + action)
      try { 
        dynamoClient.client.deleteTable(action.input.name)
      } catch {
        case e: Exception => e.printStackTrace
      }
      (action.input, action.state.deleting)
    }
  }

  implicit def deleteTableExecute[A <: AnyDeleteTable]
    (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]): DeleteTableExecute[A] = DeleteTableExecute[A](dynamoClient)


  case class CreateHashKeyTableExecute[A <: AnyCreateTable.withHashKeyTable](
      dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region],
      getAttributeDefinition: A#Input#HashKey => AttributeDefinition
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
      dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region],
      getAttributeDefinition: A#Input#HashKey => AttributeDefinition
    ): CreateHashKeyTableExecute[A] =
       CreateHashKeyTableExecute[A](dynamoClient, getAttributeDefinition)


  case class CreateCompositeKeyTableExecute[A <: AnyCreateTable.withCompositeKeyTable](
      dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region],
      getHashKeyAttributeDefinition: A#Input#HashKey => AttributeDefinition,
      getRangeKeyAttributeDefinition: A#Input#RangeKey => AttributeDefinition
    ) extends Executor {

    override type Action = A

    override def apply(ac: A): Out = {

      val table = ac.input
      println("executing: " + ac)
     // println(getHashDefinition(ac.input.hashKey))

      val hashAttributeDefinition = getHashKeyAttributeDefinition(ac.input.hashKey)
      val rangeAttributeDefinition = getRangeKeyAttributeDefinition(ac.input.rangeKey)
      val hashSchemaElement = new KeySchemaElement(ac.input.hashKey.label, "HASH")
      val rangeSchemaElement = new KeySchemaElement(ac.input.rangeKey.label, "RANGE")
      val throughput = new ProvisionedThroughput(ac.state.initialThroughput.readCapacity, ac.state.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(hashSchemaElement, rangeSchemaElement)
        .withAttributeDefinitions(hashAttributeDefinition, rangeAttributeDefinition)

      dynamoClient.client.createTable(request)

      (ac.input, ac.state.creating)
    }

    override type C[+X] = X
  }

  implicit def createCompositeKeyTableExecute[A <: AnyCreateTable.withCompositeKeyTable](implicit 
      dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region],
      getHashKeyAttributeDefinition: A#Input#HashKey => AttributeDefinition,
      getRangeKeyAttributeDefinition: A#Input#RangeKey => AttributeDefinition
    ): CreateCompositeKeyTableExecute[A] =
       CreateCompositeKeyTableExecute[A](dynamoClient, getHashKeyAttributeDefinition, getRangeKeyAttributeDefinition)

  case class DescribeTableExecute[A <: AnyDescribeTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]) extends Executor {

    override type Action = A

    override def apply(action: A): Out = {
      println("executing: " + action)
      val table = action.input
      //CREATING, UPDATING, DELETING, ACTIVE
      val newState = dynamoClient.client.describeTable(table.name).getTable.getTableStatus match {
        case "ACTIVE" => Active(table, action.state.account, action.state.throughputStatus)
        case "CREATING" => Creating(table, action.state.account, action.state.throughputStatus)
        case "DELETING" => Deleting(table, action.state.account, action.state.throughputStatus)
        case "UPDATING" => Updating(table, action.state.account, action.state.throughputStatus)
      }

      (action.input, newState)
    }

    override type C[+X] = X

  }

  implicit def describeTableExecute[A <: AnyDescribeTable]
    (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]): DescribeTableExecute[A] =
      DescribeTableExecute[A](dynamoClient)

}
