package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import shapeless._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait AnyItem extends AnyRecord { item =>
  val label: String

  /* The table is accessible through the item type */
  type Table <: AnyTable
  val  table: Table

  val  propertiesHaveValidTypes: everyElementOf[Raw]#isOneOf[ValidValues]
}

class Item[T <: AnyTable, Ps <: TypeSet, RawProperties <: TypeSet]
  (val table: T, val properties: Ps)
  (implicit 
    val representedProperties: Represented.By[Ps, RawProperties],
    val propertiesBound: Ps << AnyProperty,
    val propertiesHaveValidTypes: everyElementOf[RawProperties]#isOneOf[ValidValues]
  ) extends AnyItem {

  val label = this.toString

  type Table = T
  type Properties = Ps
  type Raw = RawProperties
}

object AnyItem {
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
}

//////////////////////////////////////////////

trait ToItem[In, I <: Singleton with AnyItem] {
  type Out = I#Rep
  type Fun <: Singleton with Poly
  def apply(in: In, i: I): Out
}

object ToItem {
  type Aux[In, I <: Singleton with AnyItem, F <: Singleton with Poly] = ToItem[In, I] { type Fun = F }

  implicit def buah[In, I <: Singleton with AnyItem, F <: Singleton with Poly, Out]
    (implicit fr: ToProperties.Aux[In, I#Properties, I#Raw, F]): ToItem.Aux[In, I, F] =
      new ToItem[In, I] {
        type Fun = F
        def apply(in: In, i: I): Out = (i: I) ->> fr(in, i.properties)
      }
}

object From {

  type Item[I <: AnyItem, Out] = FromProperties[I#Properties, Out] { type Reps = I#Raw }
  type ItemAux[I <: AnyItem, F <: Singleton with Poly, Out] = 
    FromProperties[I#Properties, Out] { 
      type Reps = I#Raw
      type Fun = F
    }
}
