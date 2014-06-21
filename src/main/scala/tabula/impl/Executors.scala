package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._
import java.util.Date

object Executors {

  /* DELETE table */
  // TODO check region of client
  implicit def deleteTableExecute[A <: AnyDeleteTable](a: A)
    (implicit 
      dynamoClient: AnyDynamoDBClient
    ): DeleteTableExecute[A] = 
       DeleteTableExecute[A](a)(dynamoClient)

  case class DeleteTableExecute[A <: AnyDeleteTable](a: A)(
      dynamoClient: AnyDynamoDBClient
    ) extends Executor(a) {
    type OutC[+X] = X

    def apply(): Out = {
      println("executing: " + action)
      try { 
        dynamoClient.client.deleteTable(action.table.name)
      } catch {
        case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
      }
      (None, action.table, action.inputState.deleting)
    }
  }


  /* CREATE table */
  implicit def createHashKeyTableExecute[A <: AnyCreateTable with AnyTableAction.withHashKeyTable](a: A)
    (implicit 
      dynamoClient: AnyDynamoDBClient
    ): CreateHashKeyTableExecute[A] =
       CreateHashKeyTableExecute[A](a)(dynamoClient)

  case class CreateHashKeyTableExecute[A <: AnyCreateTable with AnyTableAction.withHashKeyTable](a: A)
    (implicit
      dynamoClient: AnyDynamoDBClient
    ) extends Executor[A](a) {

    type OutC[+X] = X

    def apply(): Out = {
      println("executing: " + action)

      val attributeDefinition = Implicits.getAttrDef(a.table.hashKey)
      val keySchemaElement = new KeySchemaElement(action.table.hashKey.label, "HASH")
      val throughput = new ProvisionedThroughput(action.inputState.initialThroughput.readCapacity, action.inputState.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(action.table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(keySchemaElement)
        .withAttributeDefinitions(attributeDefinition)

      try {
        dynamoClient.client.createTable(request)
      } catch {
        case e: ResourceInUseException => println("warning: table " + action.table.name + " is in use")
      }

      (None, action.table, action.inputState.creating)
    }
  }


  implicit def createCompositeKeyTableExecute[A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A)
    (implicit 
      dynamoClient: AnyDynamoDBClient
    ): CreateCompositeKeyTableExecute[A] =
       CreateCompositeKeyTableExecute[A](a)(dynamoClient)

  case class CreateCompositeKeyTableExecute[A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A)(
      dynamoClient: AnyDynamoDBClient
    ) extends Executor(a) {

    type OutC[+X] = X

    def apply(): Out = {
      println("executing: " + action)

      val hashAttributeDefinition = Implicits.getAttrDef(a.table.hashKey)
      val rangeAttributeDefinition = Implicits.getAttrDef(a.table.rangeKey)
      val hashSchemaElement = new KeySchemaElement(action.table.hashKey.label, "HASH")
      val rangeSchemaElement = new KeySchemaElement(action.table.rangeKey.label, "RANGE")
      val throughput = new ProvisionedThroughput(action.inputState.initialThroughput.readCapacity, action.inputState.initialThroughput.writeCapacity)

      val request = new CreateTableRequest()
        .withTableName(action.table.name)
        .withProvisionedThroughput(throughput)
        .withKeySchema(hashSchemaElement, rangeSchemaElement)
        .withAttributeDefinitions(hashAttributeDefinition, rangeAttributeDefinition)

      try {
        dynamoClient.client.createTable(request)
      } catch {
        case t: ResourceInUseException => println("already exists")
      }

      (None, action.table, action.inputState.creating)
    }
  }


  /* DESCRIBE table */
  implicit def describeTableExecute[A <: AnyDescribeTable](a: A)
    (implicit 
      dynamoClient: AnyDynamoDBClient
    ): DescribeTableExecute[A] =
       DescribeTableExecute[A](a)(dynamoClient)

  case class DescribeTableExecute[A <: AnyDescribeTable](a: A)(
      dynamoClient: AnyDynamoDBClient
    ) extends Executor[A](a) {

    type OutC[+X] = X

    def apply(): Out = {
      println("executing: " + action)
      //CREATING, UPDATING, DELETING, ACTIVE

      val tableDescription = dynamoClient.client.describeTable(action.table.name).getTable
      val throughputDescription = tableDescription.getProvisionedThroughput

      val throughput = ohnosequences.tabula.ThroughputStatus (
        readCapacity = throughputDescription.getReadCapacityUnits.toInt,
        writeCapacity = throughputDescription.getWriteCapacityUnits.toInt,
        lastIncrease = throughputDescription.getLastIncreaseDateTime, // todo it will return null if no update actions was performed
        lastDecrease = throughputDescription.getLastDecreaseDateTime,
        numberOfDecreasesToday = throughputDescription.getNumberOfDecreasesToday.toInt
      )

      val newState: action.OutputState = dynamoClient.client.describeTable(action.table.name).getTable.getTableStatus match {
        case "ACTIVE" =>     Active(action.table, action.inputState.account, throughput)
        case "CREATING" => Creating(action.table, action.inputState.account, throughput)
        case "DELETING" => Deleting(action.table, action.inputState.account, throughput)
        case "UPDATING" => Updating(action.table, action.inputState.account, throughput)
      }

      (None, action.table, newState)
    }
  }


  /* PUT ITEM */
  implicit def putItemCompositeKeyExecutor[A <: AnyPutItemCompositeKey](a: A)
    (implicit 
      dynamoClient: AnyDynamoDBClient
    ): PutItemCompositeKeyExecutor[A] =
       PutItemCompositeKeyExecutor[A](a)(dynamoClient)

  case class PutItemCompositeKeyExecutor[A <: AnyPutItemCompositeKey](a: A)(
      dynamoClient: AnyDynamoDBClient
    ) extends Executor[A](a) {

    type OutC[+X] = X

    import scala.collection.JavaConversions._
    def apply(): Out = {
      val res: ohnosequences.tabula.PutItemResult = try {
        // println("input sdk rep: "+action.inputSDKRep.toString)
        dynamoClient.client.putItem(action.table.name, action.inputSDKRep); PutItemSuccess
      } catch {
        case t: Throwable => t.printStackTrace(); PutItemFail
      }
      (res, action.table, action.inputState)
    }
  }


  /* GET ITEM */
  implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKey](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      GetItemCompositeKeyExecutor[A] =
      GetItemCompositeKeyExecutor[A](a)(dynamoClient)

  case class GetItemCompositeKeyExecutor[A <: AnyGetItemCompositeKey](a: A)(
     dynamoClient: AnyDynamoDBClient
  ) extends Executor[A](a) {

    type OutC[+X] = X

    import scala.collection.JavaConversions._
    def apply(): Out = {
      val res: A#Output = try {
        val sdkRep = dynamoClient.client.getItem(action.table.name, Map(
          action.table.hashKey.label -> Implicits.getAttrVal(action.input._1),
          action.table.rangeKey.label -> Implicits.getAttrVal(action.input._2)
        )).getItem
        println("SDK REP: " + sdkRep.toString)
        GetItemSuccess(action.parseSDKRep(sdkRep.toMap))
      } catch {
        case t: Throwable => t.printStackTrace(); GetItemFail[action.Item]
      }
      (res, action.table, action.inputState)
    }
  }

//   case class UpdateTableExecute[A <: AnyUpdateTable](dynamoClient: AnyDynamoDBClient) extends Executor {

//     type Action = A

//     def apply(action: A): Out = {
//       println("executing: " + action)
//       val table = action.input
//       //CREATING, UPDATING, DELETING, ACTIVE

//       //todo add checks for inputState!!!
//       dynamoClient.client.updateTable(action.table.name, new ProvisionedThroughput(action.newReadThroughput, action.newWriteThroughput))


//       val oldThroughputStatus =  action.inputState.throughputStatus

//       var throughputStatus = ohnosequences.tabula.ThroughputStatus (
//         readCapacity = action.newReadThroughput,
//         writeCapacity = action.newWriteThroughput
//       )


//       //todo check it in documentation

//       //decrease
//       if (oldThroughputStatus.readCapacity > action.newReadThroughput) {
//         throughputStatus = throughputStatus.copy(
//           numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
//             lastDecrease = new Date()
//         )
//       }

//       //decrease
//       if (oldThroughputStatus.writeCapacity > action.newWriteThroughput) {
//         throughputStatus = throughputStatus.copy(
//           numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
//           lastDecrease = new Date()
//         )
//       }

//       //increase
//       if (oldThroughputStatus.readCapacity < action.newReadThroughput) {
//         throughputStatus = throughputStatus.copy(
//           lastIncrease = new Date()
//         )
//       }

//       //increase
//       if (oldThroughputStatus.writeCapacity < action.newWriteThroughput) {
//         throughputStatus = throughputStatus.copy(
//           lastIncrease = new Date()
//         )
//       }

//       val newState = Updating(action.table, action.inputState.account, throughputStatus)

//       (None, action.table, newState)
//     }

//     type C[+X] = X

//   }

//   implicit def updateTableExecute[A <: AnyUpdateTable]
//   (implicit dynamoClient: AnyDynamoDBClient): UpdateTableExecute[A] =
//     UpdateTableExecute[A](dynamoClient)

//   case class DeleteItemHashKeyExecutor[A <: AnyDeleteItemHashKey](dynamoClient: AnyDynamoDBClient, getAttributeValue: A#Table#HashKey#Raw => AttributeValue) extends Executor {

//     import scala.collection.JavaConversions._
//     type Action = A
//     type C[+X] = X

//     def apply(action: A): Out = {
//       try {
//         dynamoClient.client.deleteItem(action.table.name, Map(action.table.hashKey.label -> getAttributeValue(action.hashKeyValue)))
//       } catch {
//         case t: Throwable => t.printStackTrace()
//       }
//       (None, action.table, action.inputState)
//     }
//   }

//   implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKey]
//   (implicit dynamoClient: AnyDynamoDBClient, getAttributeValue: A#Table#HashKey#Raw => AttributeValue): DeleteItemHashKeyExecutor[A] =
//     DeleteItemHashKeyExecutor[A](dynamoClient, getAttributeValue)

//   case class DeleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKey](
//     dynamoClient: AnyDynamoDBClient,
//     getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
//     getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
//   ) extends Executor {

//     import scala.collection.JavaConversions._
//     type Action = A
//     type C[+X] = X

//     def apply(action: A): Out = {
//       try {
//         dynamoClient.client.deleteItem(action.table.name, Map(
//           action.table.hashKey.label -> getHashAttributeValue(action.hashKeyValue),
//           action.table.rangeKey.label -> getRangeAttributeValue(action.rangeKeyValue)
//         ))
//       } catch {
//         case t: Throwable => t.printStackTrace()
//       }
//       (None, action.table, action.inputState)
//     }
//   }

//   implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKey]
//   (implicit dynamoClient: AnyDynamoDBClient,
//    getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
//    getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue): DeleteItemCompositeKeyExecutor[A] =
//     DeleteItemCompositeKeyExecutor[A](dynamoClient, getHashAttributeValue, getRangeAttributeValue)


// //  case class GetItemCompositeKeyExecutor_[A <: AnyGetItemCompositeKey](
// //     a: A,
// //     dynamoClient: AnyDynamoDBClient,
// //     parseSDKItem: RepFromMap.Aux[A, A#ItemRep],
// //     getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
// //     getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
// //  ) extends Executor {
// //
// //    import scala.collection.JavaConversions._
// //    type Action = A
// //    val action = a
// //    type C[+X] = X
// //
// //    def apply(action: A): Out = {
// //      val res: A#Output = try {
// //        val sdkRep = dynamoClient.client.getItem(action.table.name, Map(
// //          action.table.hashKey.label -> getHashAttributeValue(action.input._1),
// //          action.table.rangeKey.label -> getRangeAttributeValue(action.input._2)
// //        )).getItem
// //        GetItemSuccess(parseSDKItem(sdkRep.toMap))
// //      } catch {
// //        case t: Throwable => t.printStackTrace(); GetItemFail[action.Item]
// //      }
// //      (res, action.table, action.inputState)
// //    }
// //  }
// //
// //
// //  implicit def getItemCompositeKeyExecutor_[A <: AnyGetItemCompositeKey]
// //  (implicit
// //   dynamoClient: AnyDynamoDBClient,
// //   parseSDKItem: RepFromMap.Aux[A, A#ItemRep],
// //   getHashAttributeValue: A#Table#HashKey#Raw => AttributeValue,
// //   getRangeAttributeValue: A#Table#RangeKey#Raw => AttributeValue
// //  ): ExecutorFrom.Aux[A, GetItemCompositeKeyExecutor_[A]] =
// //    new ExecutorFrom[A]{
// //      type Exec = GetItemCompositeKeyExecutor_[A]
// //      def apply(a: A): Exec = GetItemCompositeKeyExecutor_[A](a, dynamoClient, parseSDKItem, getHashAttributeValue, getRangeAttributeValue)
// //    }

}

// // trait RepFromMap[I <: Singleton with AnyItem] {
// //   // val a: A
// //   type Out = I#Rep
// //   def apply(m: Map[String, AttributeValue]): Out
// // }
// trait RepFromMap[A0 <: AnyGetItemCompositeKey] {
//   // type A = A0
//   type Out // = A#ItemRep
//   def apply(m: Map[String, AttributeValue]): Out
// }

// object RepFromMap {
//   type Aux[A <: AnyGetItemCompositeKey, R] = RepFromMap[A] { type Out = R }
// }
