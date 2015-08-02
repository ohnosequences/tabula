package ohnosequences.tabula

case object tables {

  import attributes._, regions._, resources._
  import ohnosequences.cosas._, types._, typeUnions._, properties._

  /*
    ## Tables

    A table contains only the static part of a table, things hat cannot be changed once the the table is created. Dynamic data lives in `AnyTableState`. The only exception to this is the `Account`; this is so because normally it is something that is retrieved dynamically from the environment.
  */

  trait AnyTable extends AnyDynamoDBResource {
    val name: String

    type PrimaryKey <: AnyPrimaryKey
    val  primaryKey: PrimaryKey

    type ResourceType = TableResourceType.type
    val  resourceType = TableResourceType

    type Region <: AnyRegion
    val  region: Region
  }

  class Table [
    PK <: AnyPrimaryKey,
    R <: AnyRegion
  ](val name: String,
    val primaryKey: PK,
    val region: R
  ) extends AnyTable {

    type PrimaryKey = PK
    type Region = R
  }

  object AnyTable {
    type inRegion[R <: AnyRegion] = AnyTable { type Region = R }

    type withHashKey      = AnyTable { type PrimaryKey <: AnyHashKey }
    type withCompositeKey = AnyTable { type PrimaryKey <: AnyCompositeKey }
  }


  /*
    Tables can have two types of primary keys: simple or composite. This is static and affects the operations that can be performed on them. For example, a `query` operation only makes sense on a table with a composite key.
  */
  sealed trait AnyPrimaryKey extends AnyType

  sealed trait AnyHashKey extends AnyPrimaryKey {
    type Hash <: AnyAttribute
    val  hash: Hash

    type Raw = Hash#Raw

    // should be provided implicitly:
    val hashHasValidType: Hash#Raw isOneOf PrimaryKeyValues

    lazy val label: String = s"HashKey(${hash.label})"
  }

  case class HashKey[H <: AnyAttribute](
    val hash: H
  )(implicit
    val hashHasValidType: H#Raw isOneOf PrimaryKeyValues
  ) extends AnyHashKey{ type Hash = H }


  sealed trait AnyCompositeKey extends AnyPrimaryKey {
    type Hash <: AnyAttribute
    val  hash: Hash

    type Range <: AnyAttribute
    val  range: Range

    type Raw = (Hash#Raw, Range#Raw)

    // should be provided implicitly:
    val  hashHasValidType:  Hash#Raw isOneOf PrimaryKeyValues
    val rangeHasValidType: Range#Raw isOneOf PrimaryKeyValues

    lazy val label: String = s"CompositeKey(${hash.label}, ${range.label})"
  }

  case class CompositeKey[H <: AnyAttribute, R <: AnyAttribute](
    val hash: H,
    val range: R
  )(implicit
    val  hashHasValidType: H#Raw isOneOf PrimaryKeyValues,
    val rangeHasValidType: R#Raw isOneOf PrimaryKeyValues
  ) extends AnyCompositeKey {

    type Hash = H
    type Range = R
  }

  // sealed trait PrimaryKeyValue[PK <: AnyPrimaryKey]
  // case class HashKeyValue[K <: AnyHashKey](hash: K#Hash#Raw) extends PrimaryKeyValue[K]
  // case class CompositeKeyValue[K <: AnyCompositeKey](hash: K#Hash#Raw, range: K#Range#Raw) extends PrimaryKeyValue[K]

}
