package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
object DynamoDBExecutors {

  /* CREATE TABLE */
  implicit def createHashKeyTableExecutor
    [A <: AnyCreateTable with AnyTableAction.withHashKeyTable](a: A)
      (implicit dynamoClient: AnyDynamoDBClient): 
        CreateHashKeyTableExecutor[A] =
        CreateHashKeyTableExecutor[A](a)(dynamoClient)

  implicit def createCompositeKeyTableExecutor
    [A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A)
      (implicit dynamoClient: AnyDynamoDBClient): 
        CreateCompositeKeyTableExecutor[A] =
        CreateCompositeKeyTableExecutor[A](a)(dynamoClient)


  /* DELETE TABLE */
  implicit def deleteTableExecutor[A <: AnyDeleteTable](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DeleteTableExecutor[A] =
      DeleteTableExecutor[A](a)(dynamoClient)


  /* DESCRIBE TABLE */
  implicit def describeTableExecutor[A <: AnyDescribeTable](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DescribeTableExecutor[A] =
      DescribeTableExecutor[A](a)(dynamoClient)


  /* UPDATE TABLE */
  implicit def updateTableExecutor[A <: AnyUpdateTableAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      UpdateTableExecutor[A] =
      UpdateTableExecutor[A](a)(dynamoClient)


  /* PUT ITEM */
  implicit def putItemExecutor[A <: AnyPutItemAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      PutItemExecutor[A] =
      PutItemExecutor[A](a)(dynamoClient)


  /* GET ITEM */
  implicit def getItemHashKeyExecutor[A <: AnyGetItemHashKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      GetItemHashKeyExecutor[A] =
      GetItemHashKeyExecutor[A](a)(dynamoClient)

  implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      GetItemCompositeKeyExecutor[A] =
      GetItemCompositeKeyExecutor[A](a)(dynamoClient)

  /* UPDATE ITEM */
  implicit def updateItemCompositeKeyExecutor[A <: AnyUpdateItemCompositeKeyAction](a: A)
     (implicit dynamoClient: AnyDynamoDBClient):
  UpdateItemCompositeKeyExecutor[A] =
    UpdateItemCompositeKeyExecutor[A](a)(dynamoClient)


  /* DELETE ITEM */
  implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DeleteItemHashKeyExecutor[A] =
      DeleteItemHashKeyExecutor[A](a)(dynamoClient)

  implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DeleteItemCompositeKeyExecutor[A] =
      DeleteItemCompositeKeyExecutor[A](a)(dynamoClient)

}
