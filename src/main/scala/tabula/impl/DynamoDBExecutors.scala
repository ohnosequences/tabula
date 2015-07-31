package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, fns._, types._
import ohnosequences.cosas.ops.typeSets._
import ohnosequences.tabula._, ImplicitConversions._, AnyItemAction._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
case class DynamoDBExecutors(dynamoClient: AnyDynamoDBClient) {

  /* CREATE TABLE */
  implicit def createTableExecutor[A <: AnyCreateTable](a: A):
    CreateTableExecutor[A] =
    CreateTableExecutor[A](dynamoClient)


  /* DELETE TABLE */
  implicit def deleteTableExecutor[A <: AnyDeleteTable](a: A):
    DeleteTableExecutor[A] =
    DeleteTableExecutor[A](dynamoClient)


  /* DESCRIBE TABLE */
  implicit def describeTableExecutor[A <: AnyDescribeTable](a: A):
    DescribeTableExecutor[A] =
    DescribeTableExecutor[A](dynamoClient)


  /* UPDATE TABLE */
  implicit def updateTableExecutor[A <: AnyUpdateTable](a: A):
    UpdateTableExecutor[A] =
    UpdateTableExecutor[A](dynamoClient)


  /* PUT ITEM */
  implicit def putItemExecutor[A <: AnyPutItem](a: A)(implicit
      serializer: ItemOf[A]#Raw SerializeTo SDKRep
    ): PutItemExecutor[A] =
       PutItemExecutor[A](dynamoClient, serializer)


  // /* GET ITEM */
  // // implicit def getItemHashKeyExecutor[A <: AnyGetItemHashKeyAction with SDKRepParser](a: A):
  // //   GetItemHashKeyExecutor[A] =
  // //   GetItemHashKeyExecutor[A](a)(dynamoClient)

  // // implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction with SDKRepParser](a: A):
  // //   GetItemCompositeKeyExecutor[A] =
  // //   GetItemCompositeKeyExecutor[A](a)(dynamoClient)


  /* QUERY */
  // implicit def queryExecutor[A0 <: AnyQueryAction, A <: AnyQueryAction.Q[A0]](a: A)(implicit
  //     parser: (PropertiesOf[A#Item] ParseFrom SDKRep) with out[RawOf[A#Item]]
  //   ): QueryExecutor[A0, A] =
  //      QueryExecutor[A0, A](a)(dynamoClient, parser)

  // implicit def queryExecutor[A <: AnyQuery](a: A)(implicit
  //     parser: (A#Item#Properties ParseFrom SDKRep) with out[A#Item#Raw]
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
