package ohnosequences.tabula.impl

import ohnosequences.cosas._, records._, fns._, types._
import ohnosequences.cosas.ops.typeSets._
import ohnosequences.tabula._, tables._, items._
import ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
case class DynamoDBExecutors(dynamoClient: AnyDynamoDBClient) {

  /* CREATE TABLE */
  implicit def createTableExecutor[T <: AnyTable]:
    CreateTableExecutor[T] =
    CreateTableExecutor[T](dynamoClient)


  /* DELETE TABLE */
  implicit def deleteTableExecutor[T <: AnyTable]:
    DeleteTableExecutor[T] =
    DeleteTableExecutor[T](dynamoClient)


  /* DESCRIBE TABLE */
  implicit def describeTableExecutor[T <: AnyTable]:
    DescribeTableExecutor[T] =
    DescribeTableExecutor[T](dynamoClient)


  /* UPDATE TABLE */
  implicit def updateTableExecutor[T <: AnyTable]:
    UpdateTableExecutor[T] =
    UpdateTableExecutor[T](dynamoClient)


  /* PUT ITEM */
  implicit def putItemExecutor[I <: AnyItem](implicit
      serializer: I#Raw SerializeTo SDKRep
    ): PutItemExecutor[I] =
       PutItemExecutor[I](dynamoClient, serializer)

  /* DELETE ITEM */
  implicit def deleteItemExecutor[I <: AnyItem]:
    DeleteItemExecutor[I] =
    DeleteItemExecutor[I](dynamoClient)


  /* GET ITEM */
  implicit def getItemExecutor[I <: AnyItem](implicit
    parser: (I#Attributes ParseFrom SDKRep) { type Out = I#Raw }
  ): GetItemExecutor[I] =
     GetItemExecutor[I](dynamoClient, parser)

  /* QUERY */
  // implicit def queryExecutor[A0 <: AnyQueryAction, A <: AnyQueryAction.Q[A0]](a: A)(implicit
  //     parser: (PropertiesOf[A#Item] ParseFrom SDKRep) with out[RawOf[A#Item]]
  //   ): QueryExecutor[A0, A] =
  //      QueryExecutor[A0, A](a)(dynamoClient, parser)

  // implicit def queryExecutor[A <: AnyQuery](a: A)(implicit
  //     parser: (A#Item#Properties ParseFrom SDKRep) with out[A#Item#Raw]
  //   ): QueryExecutor[A] =
  //      QueryExecutor[A](dynamoClient, parser)
}
