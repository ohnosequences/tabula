package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
case class DynamoDBExecutors(dynamoClient: AnyDynamoDBClient) {

  /* CREATE TABLE */
  implicit def createTableExecutor[A <: AnyCreateTable]:
    CreateTableExecutor[A] =
    CreateTableExecutor[A](dynamoClient)


  /* DELETE TABLE */
  implicit def deleteTableExecutor[A <: AnyDeleteTable]:
    DeleteTableExecutor[A] =
    DeleteTableExecutor[A](dynamoClient)


  /* DESCRIBE TABLE */
  implicit def describeTableExecutor[A <: AnyDescribeTable]:
    DescribeTableExecutor[A] =
    DescribeTableExecutor[A](dynamoClient)


  /* UPDATE TABLE */
  implicit def updateTableExecutor[A <: AnyUpdateTableAction]:
    UpdateTableExecutor[A] =
    UpdateTableExecutor[A](dynamoClient)


  /* PUT ITEM */
  implicit def putItemExecutor[A <: AnyPutItemAction with SDKRepGetter]:
    PutItemExecutor[A] =
    PutItemExecutor[A](dynamoClient)


  /* GET ITEM */
  // implicit def getItemHashKeyExecutor[A <: AnyGetItemHashKeyAction with SDKRepParser](a: A):
  //   GetItemHashKeyExecutor[A] =
  //   GetItemHashKeyExecutor[A](a)(dynamoClient)

  // implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction with SDKRepParser](a: A):
  //   GetItemCompositeKeyExecutor[A] =
  //   GetItemCompositeKeyExecutor[A](a)(dynamoClient)


  /* QUERY */
  implicit def queryExecutor[A <: AnyQueryAction with SDKRepParser]:
    QueryExecutor[A] =
    QueryExecutor[A](dynamoClient)


  /* DELETE ITEM */
  // implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A):
  //   DeleteItemHashKeyExecutor[A] =
  //   DeleteItemHashKeyExecutor[A](a)(dynamoClient)

  // implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A):
  //   DeleteItemCompositeKeyExecutor[A] =
  //   DeleteItemCompositeKeyExecutor[A](a)(dynamoClient)

}
