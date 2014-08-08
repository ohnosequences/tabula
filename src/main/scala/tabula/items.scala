package ohnosequences.tabula

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import shapeless._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait AnyItem extends Representable { item =>

  val label: String

  /* The table is accessible through the item type */
  type Table <: AnyTable
  val  table: Table

  type Record <: Singleton with AnyRecord
  val record: Record

  type Raw = AnyItem.RawOf[Record]

  val  propertiesHaveValidTypes: everyElementOf[Record#Raw]#isOneOf[ValidValues]

  // double tagging FTW!
  final def fields[R <: TypeSet](r: R)(implicit 
    p: R ~> record.Raw
  ): item.Rep = item ->> (record ->> p(r))

  implicit def propertyOps(rep: TaggedWith[Record]): AnyRecord.OtherPropertyOps[Record] = 
    AnyRecord.OtherPropertyOps[Record] (
      (record:Record) ->> rep
    )
}

class Item[T <: AnyTable, R <: Singleton with AnyRecord](val table: T, val record: R)(implicit 
  val propertiesHaveValidTypes: everyElementOf[R#Raw]#isOneOf[ValidValues]
) 
  extends AnyItem 
{

  val label = this.toString

  type Table = T
  type Record = R
}

object AnyItem {

  type RawOf[R <: Singleton with AnyRecord] = R#Raw
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
  type withRecord[R <: AnyRecord] = AnyItem { type Record = R }

  type RecordOf[I <: AnyItem] = I#Record
  type PropertiesOf[I <: AnyItem] = RecordOf[I]#Properties

  implicit def propertyOps[R <: Singleton with AnyItem](entry: TaggedWith[R])(implicit
    getItem: TaggedWith[R] => R
  ): AnyRecord.OtherPropertyOps[R#Record] = {

    val uh = getItem(entry)

    AnyRecord.OtherPropertyOps(

        (uh.record:R#Record) ->> entry
      )

  }
      
}

//////////////////////////////////////////////

trait ToItem[In, I <: AnyItem] {

  type Out = TaggedWith[I]
  type Fun <: Poly
  def apply(in: In, i: I): Out
}

object ToItem {

  import AnyItem.PropertiesOf

  type Aux[In, I <: AnyItem, F <: Poly] = ToItem[In, I] { type Fun = F }

  implicit def buah[In, I <: Singleton with AnyItem, F <: Singleton with Poly, Out](implicit 
    fr: ToProperties.Aux[In, PropertiesOf[I], RawOf[I], F]
  ): ToItem.Aux[In, I, F] =

      new ToItem[In, I] {

        type Fun = F

        def apply(in: In, i: I): TaggedWith[I] = {

          val props: I#Record#Properties = i.record.properties
          val itemV: I#Raw = fr(in, props)

          ((i: I) ->> itemV): TaggedWith[I]
        }
      }
}

object From {

  type Item[I <: Singleton with AnyItem, Out] = FromProperties[I#Record#Properties, Out] { type Reps = I#Record#Raw }

  type ItemAux[I <: Singleton with AnyItem, F <: Poly, Out] = 
  
    FromProperties[I#Record#Properties, Out] { 

      type Reps = I#Record#Raw
      type Fun = F
    }
}
