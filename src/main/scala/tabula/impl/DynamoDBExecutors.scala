package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._, AnyFn._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
case class DynamoDBExecutors(dynamoClient: AnyDynamoDBClient) {

  /* CREATE TABLE */
  implicit def createTableExecutor[A <: AnyCreateTable](a: A):
    CreateTableExecutor[A] =
    CreateTableExecutor[A](a)(dynamoClient)


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
  implicit def putItemExecutor[A <: AnyPutItemAction](a: A)(implicit 
      serializer: RawOf[A#Item] SerializeTo SDKRep
    ): PutItemExecutor[A] =
       PutItemExecutor[A](a)(dynamoClient, serializer)


  // /* GET ITEM */
  // // implicit def getItemHashKeyExecutor[A <: AnyGetItemHashKeyAction with SDKRepParser](a: A):
  // //   GetItemHashKeyExecutor[A] =
  // //   GetItemHashKeyExecutor[A](a)(dynamoClient)

  // // implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction with SDKRepParser](a: A):
  // //   GetItemCompositeKeyExecutor[A] =
  // //   GetItemCompositeKeyExecutor[A](a)(dynamoClient)


  // /* QUERY */
  // implicit def queryExecutor[A <: AnyQueryAction](implicit 
  //     parser: (PropertiesOf[A#Item] ParseFrom SDKRep) with out[RawOf[A#Item]]
  //   ): QueryExecutor[A] =
  //      QueryExecutor[A](dynamoClient, parser)


  // /* DELETE ITEM */
  // // implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A):
  // //   DeleteItemHashKeyExecutor[A] =
  // //   DeleteItemHashKeyExecutor[A](a)(dynamoClient)

  // // implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A):
  // //   DeleteItemCompositeKeyExecutor[A] =
  // //   DeleteItemCompositeKeyExecutor[A](a)(dynamoClient)

}
