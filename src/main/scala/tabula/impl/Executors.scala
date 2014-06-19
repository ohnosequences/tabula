package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._
import java.util.Date

object Executors {

  case class DeleteTableExecute[A <: AnyDeleteTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]) extends Executor {
    type Action = A

    override type C[+X] = X

    override def apply(action: A): Out = {
      println("executing: " + action)
      try { 
        dynamoClient.client.deleteTable(action.input.name)
      } catch {
        case r: ResourceNotFoundException => println("warning: table " + action.input.name + " doesn't exist")
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

      try {
        dynamoClient.client.createTable(request)
      } catch {
        case e: ResourceInUseException => println("warning: table " + table.name + " is in use")
      }


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

      val tableDescription = dynamoClient.client.describeTable(table.name).getTable
      val throughputDescription = tableDescription.getProvisionedThroughput

      val throughput = ohnosequences.tabula.ThroughputStatus (
        readCapacity = throughputDescription.getReadCapacityUnits.toInt,
        writeCapacity = throughputDescription.getWriteCapacityUnits.toInt,
        lastIncrease = throughputDescription.getLastIncreaseDateTime, // todo it will return null if no update actions was performed
        lastDecrease = throughputDescription.getLastDecreaseDateTime,
        numberOfDecreasesToday = throughputDescription.getNumberOfDecreasesToday.toInt
      )

      val newState = dynamoClient.client.describeTable(table.name).getTable.getTableStatus match {
        case "ACTIVE" =>     Active(table, action.state.account, throughput)
        case "CREATING" => Creating(table, action.state.account, throughput)
        case "DELETING" => Deleting(table, action.state.account, throughput)
        case "UPDATING" => Updating(table, action.state.account, throughput)
      }
      (action.input, newState)
    }

    override type C[+X] = X

  }

  implicit def describeTableExecute[A <: AnyDescribeTable]
    (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]): DescribeTableExecute[A] =
      DescribeTableExecute[A](dynamoClient)


  case class UpdateTableExecute[A <: AnyUpdateTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]) extends Executor {

    override type Action = A

    override def apply(action: A): Out = {
      println("executing: " + action)
      val table = action.input
      //CREATING, UPDATING, DELETING, ACTIVE

      //todo add checks for state!!!
      dynamoClient.client.updateTable(table.name, new ProvisionedThroughput(action.newReadThroughput, action.newWriteThroughput))


      val oldThroughputStatus =  action.state.throughputStatus

      var throughputStatus = ohnosequences.tabula.ThroughputStatus (
        readCapacity = action.newReadThroughput,
        writeCapacity = action.newWriteThroughput
      )


      //todo check it in documentation

      //decrease
      if (oldThroughputStatus.readCapacity > action.newReadThroughput) {
        throughputStatus = throughputStatus.copy(
          numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
            lastDecrease = new Date()
        )
      }

      //decrease
      if (oldThroughputStatus.writeCapacity > action.newWriteThroughput) {
        throughputStatus = throughputStatus.copy(
          numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
          lastDecrease = new Date()
        )
      }

      //increase
      if (oldThroughputStatus.readCapacity < action.newReadThroughput) {
        throughputStatus = throughputStatus.copy(
          lastIncrease = new Date()
        )
      }

      //increase
      if (oldThroughputStatus.writeCapacity < action.newWriteThroughput) {
        throughputStatus = throughputStatus.copy(
          lastIncrease = new Date()
        )
      }

      val newState = Updating(table, action.state.account, throughputStatus)

      (action.input, newState)
    }

    override type C[+X] = X

  }

  implicit def updateTableExecute[A <: AnyUpdateTable]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region]): UpdateTableExecute[A] =
    UpdateTableExecute[A](dynamoClient)

  case class DeleteItemHashKeyExecutor[A <: AnyDeleteItemHashKey](dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region], getAttributeValue: A#Input#HashKey#Raw => AttributeValue) extends Executor {

    import scala.collection.JavaConversions._
    override type Action = A

    override def apply(action: A): Out = {

      try {

        val table = action.input
        dynamoClient.client.deleteItem(table.name, Map(table.hashKey.label -> getAttributeValue(action.hashKeyValue)))
      } catch {
        case t: Throwable => t.printStackTrace()
      }


      (action.input, action.state)
    }

    override type C[+X] = X

  }

  implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKey]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region], getAttributeValue: A#Input#HashKey#Raw => AttributeValue): DeleteItemHashKeyExecutor[A] =
    DeleteItemHashKeyExecutor[A](dynamoClient, getAttributeValue)

  case class DeleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKey](
    dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region],
    getHashAttributeValue: A#Input#HashKey#Raw => AttributeValue,
    getRangeAttributeValue: A#Input#RangeKey#Raw => AttributeValue
  ) extends Executor {

    import scala.collection.JavaConversions._
    override type Action = A

    override def apply(action: A): Out = {

      try {

        val table = action.input
        dynamoClient.client.deleteItem(table.name, Map(
          table.hashKey.label -> getHashAttributeValue(action.hashKeyValue),
          table.rangeKey.label -> getRangeAttributeValue(action.rangeKeyValue)
        ))
      } catch {
        case t: Throwable => t.printStackTrace()
      }


      (action.input, action.state)
    }

    override type C[+X] = X

  }

  implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKey]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Input#Region],
   getHashAttributeValue: A#Input#HashKey#Raw => AttributeValue,
   getRangeAttributeValue: A#Input#RangeKey#Raw => AttributeValue): DeleteItemCompositeKeyExecutor[A] =
    DeleteItemCompositeKeyExecutor[A](dynamoClient, getHashAttributeValue, getRangeAttributeValue)

}
