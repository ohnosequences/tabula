package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._
import java.util.Date

object Executors {

  case class DeleteTableExecute[A <: AnyDeleteTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]) extends Executor {
    type Action = A

    type C[+X] = X

    def apply(action: A): Out = {
      println("executing: " + action)
      try { 
        dynamoClient.client.deleteTable(action.table.name)
      } catch {
        case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
      }
      (None, action.table, action.inputState.deleting)
    }
  }

  implicit def deleteTableExecute[A <: AnyDeleteTable]
    (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]): DeleteTableExecute[A] = DeleteTableExecute[A](dynamoClient)

//  case class DeleteTableExecute_[A <: AnyDeleteTable](a: A, dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]) extends Executor {
//    type Action = A
//    val action = a
//
//    type C[+X] = X
//
//    def apply(action: A): Out = {
//      println("executing: " + action)
//      try {
//        dynamoClient.client.deleteTable(action.table.name)
//      } catch {
//        case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
//      }
//      (None, action.table, action.inputState.deleting)
//    }
//  }
//
//  implicit def deleteTableExecute_[A <: AnyDeleteTable]
//    (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]): ExecutorFrom.Aux[A, DeleteTableExecute_[A]] =
//    new ExecutorFrom[A]{
//      type Exec = DeleteTableExecute_[A]
//      def apply(a: A): Exec = DeleteTableExecute_[A](a, dynamoClient)
//    }

  case class CreateHashKeyTableExecute[A <: AnyCreateTable.withHashKeyTable](
      dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
      getAttributeDefinition: A#Table#HashKey => AttributeDefinition
    ) extends Executor {

    type Action = A
    type C[+X] = X

    def apply(ac: A): Out = {

      println("executing: " + ac)
     // println(getHashDefinition(ac.table.hashKey))

      val attributeDefinition = getAttributeDefinition(ac.table.hashKey)
      val keySchemaElement = new KeySchemaElement(ac.table.hashKey.label, "HASH")
      val throughput = new ProvisionedThroughput(ac.inputState.initialThroughput.readCapacity, ac.inputState.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(ac.table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(keySchemaElement)
        .withAttributeDefinitions(attributeDefinition)

      try {
        dynamoClient.client.createTable(request)
      } catch {
        case e: ResourceInUseException => println("warning: table " + ac.table.name + " is in use")
      }

      (None, ac.table, ac.inputState.creating)
    }
  }

  implicit def createHashKeyTableExecute[A <: AnyCreateTable.withHashKeyTable](implicit 
      dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
      getAttributeDefinition: A#Table#HashKey => AttributeDefinition
    ): CreateHashKeyTableExecute[A] =
       CreateHashKeyTableExecute[A](dynamoClient, getAttributeDefinition)


  case class CreateCompositeKeyTableExecute[A <: AnyCreateTable.withCompositeKeyTable](
      dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
      getHashKeyAttributeDefinition: A#Table#HashKey => AttributeDefinition,
      getRangeKeyAttributeDefinition: A#Table#RangeKey => AttributeDefinition
    ) extends Executor {

    type Action = A

    def apply(ac: A): Out = {

      println("executing: " + ac)
     // println(getHashDefinition(ac.table.hashKey))

      val hashAttributeDefinition = getHashKeyAttributeDefinition(ac.table.hashKey)
      val rangeAttributeDefinition = getRangeKeyAttributeDefinition(ac.table.rangeKey)
      val hashSchemaElement = new KeySchemaElement(ac.table.hashKey.label, "HASH")
      val rangeSchemaElement = new KeySchemaElement(ac.table.rangeKey.label, "RANGE")
      val throughput = new ProvisionedThroughput(ac.inputState.initialThroughput.readCapacity, ac.inputState.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(ac.table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(hashSchemaElement, rangeSchemaElement)
        .withAttributeDefinitions(hashAttributeDefinition, rangeAttributeDefinition)

      try {
        dynamoClient.client.createTable(request)
      } catch {
        case t: ResourceInUseException => println("already exists")
      }


      (None, ac.table, ac.inputState.creating)
    }

    type C[+X] = X
  }

  implicit def createCompositeKeyTableExecute[A <: AnyCreateTable.withCompositeKeyTable](implicit 
      dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
      getHashKeyAttributeDefinition: A#Table#HashKey => AttributeDefinition,
      getRangeKeyAttributeDefinition: A#Table#RangeKey => AttributeDefinition
    ): CreateCompositeKeyTableExecute[A] =
       CreateCompositeKeyTableExecute[A](dynamoClient, getHashKeyAttributeDefinition, getRangeKeyAttributeDefinition)

  case class DescribeTableExecute[A <: AnyDescribeTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]) extends Executor {

    type Action = A

    def apply(ac: A): Out = {
      println("executing: " + ac)
      val table = ac.input
      //CREATING, UPDATING, DELETING, ACTIVE

      val tableDescription = dynamoClient.client.describeTable(ac.table.name).getTable
      val throughputDescription = tableDescription.getProvisionedThroughput

      val throughput = ohnosequences.tabula.ThroughputStatus (
        readCapacity = throughputDescription.getReadCapacityUnits.toInt,
        writeCapacity = throughputDescription.getWriteCapacityUnits.toInt,
        lastIncrease = throughputDescription.getLastIncreaseDateTime, // todo it will return null if no update actions was performed
        lastDecrease = throughputDescription.getLastDecreaseDateTime,
        numberOfDecreasesToday = throughputDescription.getNumberOfDecreasesToday.toInt
      )

      val newState = dynamoClient.client.describeTable(ac.table.name).getTable.getTableStatus match {
        case "ACTIVE" =>     Active(ac.table, ac.inputState.account, throughput)
        case "CREATING" => Creating(ac.table, ac.inputState.account, throughput)
        case "DELETING" => Deleting(ac.table, ac.inputState.account, throughput)
        case "UPDATING" => Updating(ac.table, ac.inputState.account, throughput)
      }
      (None, ac.table, newState)
    }

    type C[+X] = X

  }

  implicit def describeTableExecute[A <: AnyDescribeTable]
    (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]): DescribeTableExecute[A] =
      DescribeTableExecute[A](dynamoClient)


  case class UpdateTableExecute[A <: AnyUpdateTable](dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]) extends Executor {

    type Action = A

    def apply(ac: A): Out = {
      println("executing: " + ac)
      val table = ac.input
      //CREATING, UPDATING, DELETING, ACTIVE

      //todo add checks for inputState!!!
      dynamoClient.client.updateTable(ac.table.name, new ProvisionedThroughput(ac.newReadThroughput, ac.newWriteThroughput))


      val oldThroughputStatus =  ac.inputState.throughputStatus

      var throughputStatus = ohnosequences.tabula.ThroughputStatus (
        readCapacity = ac.newReadThroughput,
        writeCapacity = ac.newWriteThroughput
      )


      //todo check it in documentation

      //decrease
      if (oldThroughputStatus.readCapacity > ac.newReadThroughput) {
        throughputStatus = throughputStatus.copy(
          numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
            lastDecrease = new Date()
        )
      }

      //decrease
      if (oldThroughputStatus.writeCapacity > ac.newWriteThroughput) {
        throughputStatus = throughputStatus.copy(
          numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
          lastDecrease = new Date()
        )
      }

      //increase
      if (oldThroughputStatus.readCapacity < ac.newReadThroughput) {
        throughputStatus = throughputStatus.copy(
          lastIncrease = new Date()
        )
      }

      //increase
      if (oldThroughputStatus.writeCapacity < ac.newWriteThroughput) {
        throughputStatus = throughputStatus.copy(
          lastIncrease = new Date()
        )
      }

      val newState = Updating(ac.table, ac.inputState.account, throughputStatus)

      (None, ac.table, newState)
    }

    type C[+X] = X

  }

  implicit def updateTableExecute[A <: AnyUpdateTable]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region]): UpdateTableExecute[A] =
    UpdateTableExecute[A](dynamoClient)

  case class DeleteItemHashKeyExecutor[A <: AnyDeleteItemHashKey](dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region], getAttributeValue: A#Table#HashKey#Raw => AttributeValue) extends Executor {

    import scala.collection.JavaConversions._
    type Action = A
    type C[+X] = X

    def apply(ac: A): Out = {
      try {
        dynamoClient.client.deleteItem(ac.table.name, Map(ac.table.hashKey.label -> getAttributeValue(ac.hashKeyValue)))
      } catch {
        case t: Throwable => t.printStackTrace()
      }
      (None, ac.table, ac.inputState)
    }
  }

  implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKey]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region], getAttributeValue: A#Table#HashKey#Raw => AttributeValue): DeleteItemHashKeyExecutor[A] =
    DeleteItemHashKeyExecutor[A](dynamoClient, getAttributeValue)

  case class DeleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKey](
    dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
    getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
    getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
  ) extends Executor {

    import scala.collection.JavaConversions._
    type Action = A
    type C[+X] = X

    def apply(ac: A): Out = {
      try {
        dynamoClient.client.deleteItem(ac.table.name, Map(
          ac.table.hashKey.label -> getHashAttributeValue(ac.hashKeyValue),
          ac.table.rangeKey.label -> getRangeAttributeValue(ac.rangeKeyValue)
        ))
      } catch {
        case t: Throwable => t.printStackTrace()
      }
      (None, ac.table, ac.inputState)
    }
  }

  implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKey]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
   getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
   getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue): DeleteItemCompositeKeyExecutor[A] =
    DeleteItemCompositeKeyExecutor[A](dynamoClient, getHashAttributeValue, getRangeAttributeValue)


  case class PutItemCompositeKeyExecutor[A <: AnyPutItemCompositeKey](
    dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
    getSDKRep: A#ItemRep => Map[String, AttributeValue]
  ) extends Executor {

    import scala.collection.JavaConversions._
    type Action = A
    type C[+X] = X

    def apply(ac: A): Out = {
      val res = try {
        dynamoClient.client.putItem(ac.table.name, getSDKRep(ac.input._2)); PutItemSuccess
      } catch {
        case t: Throwable => t.printStackTrace(); PutItemFail
      }
      (res, ac.table, ac.inputState)
    }
  }

  implicit def putItemCompositeKeyExecutor[A <: AnyPutItemCompositeKey]
  (implicit dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
   getSDKRep: A#ItemRep => Map[String, AttributeValue]): PutItemCompositeKeyExecutor[A] =
    PutItemCompositeKeyExecutor[A](dynamoClient, getSDKRep)

  case class GetItemCompositeKeyExecutor[A <: AnyGetItemCompositeKey](
     dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
    // parseSDKItem: RepFromMap.Aux[A, A#ItemRep],
      parseSDKItem: Map[String, AttributeValue] => A#ItemRaw,
     getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
     getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
  ) extends Executor {

    import scala.collection.JavaConversions._
    type Action = A
    type C[+X] = X

    def apply(ac: A): Out = {
      val res: A#Output = try {
        val sdkRep = dynamoClient.client.getItem(ac.table.name, Map(
          ac.table.hashKey.label -> getHashAttributeValue(ac.input._1),
          ac.table.rangeKey.label -> getRangeAttributeValue(ac.input._2)
        )).getItem
        GetItemSuccess(parseSDKItem(sdkRep.toMap))
      } catch {
        case t: Throwable => t.printStackTrace(); GetItemFail[ac.Item]
      }
      (res, ac.table, ac.inputState)
    }
  }

  implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKey]
  (implicit 
   dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
   parseSDKItem: Map[String, AttributeValue] => A#ItemRaw,
   getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
   getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue): GetItemCompositeKeyExecutor[A] =
    GetItemCompositeKeyExecutor[A](dynamoClient, parseSDKItem, getHashAttributeValue, getRangeAttributeValue)


//  case class GetItemCompositeKeyExecutor_[A <: AnyGetItemCompositeKey](
//     a: A,
//     dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
//     parseSDKItem: RepFromMap.Aux[A, A#ItemRep],
//     getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
//     getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
//  ) extends Executor {
//
//    import scala.collection.JavaConversions._
//    type Action = A
//    val action = a
//    type C[+X] = X
//
//    def apply(ac: A): Out = {
//      val res: A#Output = try {
//        val sdkRep = dynamoClient.client.getItem(ac.table.name, Map(
//          ac.table.hashKey.label -> getHashAttributeValue(ac.input._1),
//          ac.table.rangeKey.label -> getRangeAttributeValue(ac.input._2)
//        )).getItem
//        GetItemSuccess(parseSDKItem(sdkRep.toMap))
//      } catch {
//        case t: Throwable => t.printStackTrace(); GetItemFail[ac.Item]
//      }
//      (res, ac.table, ac.inputState)
//    }
//  }
//
//
//  implicit def getItemCompositeKeyExecutor_[A <: AnyGetItemCompositeKey]
//  (implicit
//   dynamoClient: AnyDynamoDBClient.inRegion[A#Table#Region],
//   parseSDKItem: RepFromMap.Aux[A, A#ItemRep],
//   getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
//   getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
//  ): ExecutorFrom.Aux[A, GetItemCompositeKeyExecutor_[A]] =
//    new ExecutorFrom[A]{
//      type Exec = GetItemCompositeKeyExecutor_[A]
//      def apply(a: A): Exec = GetItemCompositeKeyExecutor_[A](a, dynamoClient, parseSDKItem, getHashAttributeValue, getRangeAttributeValue)
//    }

}

// trait RepFromMap[I <: Singleton with AnyItem] {
//   // val a: A
//   type Out = I#Rep
//   def apply(m: Map[String, AttributeValue]): Out
// }
trait RepFromMap[A0 <: AnyGetItemCompositeKey] {
  // type A = A0
  type Out // = A#ItemRep
  def apply(m: Map[String, AttributeValue]): Out
}

object RepFromMap {
  type Aux[A <: AnyGetItemCompositeKey, R] = RepFromMap[A] { type Out = R }
}
