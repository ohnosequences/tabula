package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
case class DynamoDBExecutors(dynamoClient: AnyDynamoDBClient) {

  /* CREATE TABLE */
  // implicit def createHashKeyTableExecutor
  //   [A <: AnyCreateTable with AnyTableAction.withHashKeyTable](a: A):
  //     CreateHashKeyTableExecutor[A] =
  //     CreateHashKeyTableExecutor[A](a)(dynamoClient)

  // implicit def createCompositeKeyTableExecutor
  //   [A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A):
  //     CreateCompositeKeyTableExecutor[A] =
  //     CreateCompositeKeyTableExecutor[A](a)(dynamoClient)


  /* DELETE TABLE */
  implicit def deleteTableExecutor[A <: AnyDeleteTable](a: A):
    DeleteTableExecutor[A] =
    DeleteTableExecutor[A](a)(dynamoClient)


  /* DESCRIBE TABLE */
  implicit def describeTableExecutor[A <: AnyDescribeTable](a: A):
    DescribeTableExecutor[A] =
    DescribeTableExecutor[A](a)(dynamoClient)


  /* UPDATE TABLE */
  implicit def updateTableExecutor[A <: AnyUpdateTableAction](a: A):
    UpdateTableExecutor[A] =
    UpdateTableExecutor[A](a)(dynamoClient)


  /* PUT ITEM */
  implicit def putItemExecutor[A <: AnyPutItemAction with SDKRepGetter](a: A):
    PutItemExecutor[A] =
    PutItemExecutor[A](a)(dynamoClient)


  /* GET ITEM */
  // implicit def getItemHashKeyExecutor[A <: AnyGetItemHashKeyAction with SDKRepParser](a: A):
  //   GetItemHashKeyExecutor[A] =
  //   GetItemHashKeyExecutor[A](a)(dynamoClient)

  // implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction with SDKRepParser](a: A):
  //   GetItemCompositeKeyExecutor[A] =
  //   GetItemCompositeKeyExecutor[A](a)(dynamoClient)


  /* QUERY */
  implicit def queryExecutor[A <: AnyQueryAction with SDKRepParser](a: A):
    QueryExecutor[A] =
    QueryExecutor[A](a)(dynamoClient)


  /* DELETE ITEM */
  // implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A):
  //   DeleteItemHashKeyExecutor[A] =
  //   DeleteItemHashKeyExecutor[A](a)(dynamoClient)

  // implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A):
  //   DeleteItemCompositeKeyExecutor[A] =
  //   DeleteItemCompositeKeyExecutor[A](a)(dynamoClient)

}
