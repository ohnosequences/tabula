package ohnosequences.tabula.impl

import ohnosequences.pointless._, AnyRecord._, AnyFn._
import ohnosequences.pointless.ops.typeSet._
import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
case class DynamoDBExecutors(dynamoClient: AnyDynamoDBClient) {

  /* CREATE TABLE */
  implicit def createTableExecutor[T <: AnyTable, A <: CreateTable[T]]:
    CreateTableExecutor[T, A] =
    CreateTableExecutor[T, A](dynamoClient)


  /* DELETE TABLE */
  implicit def deleteTableExecutor[T <: AnyTable, A <: DeleteTable[T]]:
    DeleteTableExecutor[T, A] =
    DeleteTableExecutor[T, A](dynamoClient)


  /* DESCRIBE TABLE */
  implicit def describeTableExecutor[T <: AnyTable, A <: DescribeTable[T]](t: T, a: A):
    DescribeTableExecutor[T, A] =
    DescribeTableExecutor[T, A](dynamoClient)


  /* UPDATE TABLE */
  implicit def updateTableExecutor[T <: AnyTable, A <: UpdateTable[T]]:
    UpdateTableExecutor[T, A] =
    UpdateTableExecutor[T, A](dynamoClient)


  /* PUT ITEM */
  implicit def putItemExecutor[I <: AnyItem, A <: PutItem[I]](implicit 
      serializer: RawOf[I] SerializeTo SDKRep
    ): PutItemExecutor[I, A] =
       PutItemExecutor[I, A](dynamoClient, serializer)


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

  implicit def queryExecutor[I <: AnyItem.OfCompositeTable, A <: QueryActionFor[I]](implicit 
      parser: (PropertiesOf[I] ParseFrom SDKRep) with out[RawOf[I]]
    ): QueryExecutor[I, A] =
       QueryExecutor[I, A](dynamoClient, parser)


  // /* DELETE ITEM */
  // // implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A):
  // //   DeleteItemHashKeyExecutor[A] =
  // //   DeleteItemHashKeyExecutor[A](a)(dynamoClient)

  // // implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A):
  // //   DeleteItemCompositeKeyExecutor[A] =
  // //   DeleteItemCompositeKeyExecutor[A](a)(dynamoClient)

}
