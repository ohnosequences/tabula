package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._
import shapeless._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait AnyItem extends AnyTaggedType { item =>

  val label: String

  /* The table is accessible through the item type */
  type Table <: AnyTable
  val  table: Table

  type Record <: AnyRecord
  val record: Record

  type Raw = RawOf[Record]

  implicit val propertiesHaveValidTypes: Raw isBoundedByUnion ValidValues

  // double tagging FTW!
  final def fields[R <: AnyTypeSet](r: R)(implicit 
    p: R As Raw
  ): Tagged[Me] = (item:Me) =>> (record =>> p(r))

  implicit def propertyOps(rep: Tagged[Record]): AnyRecord.OtherPropertyOps[Record] = 
    AnyRecord.OtherPropertyOps[Record] (
      (record:Record) =>> rep
    )
}

abstract class Item[T <: AnyTable, R <: AnyRecord](val table: T, val rc: R)
(implicit 
  val propertiesHaveValidTypes: RawOf[R] isBoundedByUnion ValidValues
) 
  extends AnyItem 
{

  val label = this.toString

  type Table = T
  type Record = R
}

object AnyItem {

  type RawOf[R <: AnyRecord] = R#Raw
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
  type withRecord[R <: AnyRecord] = AnyItem { type Record = R }

  type RecordOf[I <: AnyItem] = I#Record
  type PropertiesOf[I <: AnyItem] = RecordOf[I]#Properties

  // implicit def propertyOps[R <: AnyItem](entry: Tagged[R])(implicit
  //   getItem: Tagged[R] => R
  // ): AnyRecord.OtherPropertyOps[R#Record] = {

  //   val uh = getItem(entry)

  //   AnyRecord.OtherPropertyOps(

  //       (uh.record:R#Record) ->> entry
  //     )

  // }
      
}

//////////////////////////////////////////////

trait ToItem[In, I <: AnyItem] {

  type Out = Tagged[I]
  type Fun <: Singleton with Poly
  def apply(in: In, i: I): Out
}

object ToItem {

  import AnyItem.PropertiesOf

  type Aux[In, I <: AnyItem, F <: Singleton with Poly] = ToItem[In, I] { type Fun = F }

  implicit def buah[In, I <: AnyItem, F <: Singleton with Poly, Out](implicit 
    fr: ToProperties.Aux[In, I#Record#Properties, RawOf[I], F]
  ): ToItem.Aux[In, I, F] =

      new ToItem[In, I] {

        type Fun = F

        def apply(in: In, i: I): Tagged[I] = {

          val rec = i.record
          val props = rec.properties
          val itemV = fr(in, props)

          ((i:I) =>> itemV)
        }
      }
}

object From {

  type Item[I <: AnyItem, Out] = FromProperties[I#Record#Properties, Out] { type Reps = I#Record#Raw }

  type ItemAux[I <: AnyItem, F <: Poly, Out] = 
  
    FromProperties[I#Record#Properties, Out] { 

      type Reps = I#Record#Raw
      type Fun = F
    }
}
