package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeSet._
import shapeless._, poly._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait AnyItem extends AnyRecord {

  type Table <: AnyTable
  val  table: Table

  type Raw <: AnyTypeSet.BoundedByUnion[ValidValues]

  // type Raw <: AnyTypeSet.Of[ValidValues]
}

class Item [
  T  <: AnyTable,
  Props <: AnyTypeSet.Of[AnyProperty],
  Vals <: AnyTypeSet.BoundedByUnion[ValidValues]
]
(
  val label: String,
  val table: T,
  val properties: Props
)(implicit
  val representedProperties: Props isRepresentedBy Vals
) 
extends AnyItem {

  type Properties = Props
  type Raw = Vals
  type Table = T
}

object AnyItem {

  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
  type withProperties[P <: AnyTypeSet with AnyTypeSet.Of[AnyProperty]] = AnyItem { type Props = P }
}

trait ListLike[L] {
  type E // elements type

  val nil: L
  def cons(h: E, t: L): L

  def head(l: L): E
  def tail(l: L): L
}

object ListLike {
  type Of[L, T] = ListLike[L] { type E = T }
}

/* Transforms record to something else */
trait FromProperties[
    A <: AnyTypeSet, // set of properties
    Out           // what we want to get
  ] {

  type Reps <: AnyTypeSet         // representation of properties
  type Fun <: Singleton with Poly1 // transformation function

  def apply(r: Reps): Out
}

object FromProperties {
  
  def apply[A <: AnyTypeSet, Reps <: AnyTypeSet, F <: Singleton with Poly1, Out](implicit tr: FromProperties.Aux[A, Reps, F, Out]):
    FromProperties.Aux[A, Reps, F, Out] = tr

  type Aux[A <: AnyTypeSet, R <: AnyTypeSet, F <: Singleton with Poly1, Out] =
    FromProperties[A, Out] { 
      type Reps = R
      type Fun = F
    }

  type Anyhow[A <: AnyTypeSet, R <: AnyTypeSet, Out] =
    FromProperties[A, Out] { 
      type Reps = R
    }

  implicit def empty[Out, F <: Singleton with Poly1]
    (implicit m: ListLike[Out]): FromProperties.Aux[∅, ∅, F, Out] = new FromProperties[∅, Out] {
      type Reps = ∅
      type Fun = F
      def apply(r: ∅): Out = m.nil
    }

  implicit def cons[
    F <: Singleton with Poly1,
    AH <: Singleton with AnyProperty, AT <: AnyTypeSet,
    RT <: AnyTypeSet,
    E, Out
  ](implicit
    tagOf: Tagged[AH] => AH,
    listLike: ListLike.Of[Out, E], 
    transform: Case1.Aux[F, (AH, Tagged[AH]), E], 
    recOnTail: FromProperties.Aux[AT, RT, F, Out]
  ): FromProperties.Aux[AH :~: AT, Tagged[AH] :~: RT, F, Out] =
    new FromProperties[AH :~: AT, Out] {
      type Reps = Tagged[AH] :~: RT
      type Fun = F
      def apply(r: Tagged[AH] :~: RT): Out = {
        listLike.cons(
          transform((tagOf(r.head), r.head)),
          recOnTail(r.tail)
        )
      }
    }
}

// ///////////////////////////////////////////////////////////////

/* Transforms properties set representation from something else */
trait ToProperties[
    In,          // some other representation
    A <: AnyTypeSet // set of corresponding properties
  ] {

  type Out <: AnyTypeSet             // representation of properties
  type Fun <: Singleton with Poly1 // transformation function

  def apply(in: In, a: A): Out
}

object ToProperties {
  type Aux[In, A <: AnyTypeSet, O <: AnyTypeSet, F <: Singleton with Poly1] = ToProperties[In, A] { type Out = O; type Fun = F } 

  def apply[In, A <: AnyTypeSet, O <: AnyTypeSet, F <: Singleton with Poly1]
    (implicit form: ToProperties.Aux[In, A, O, F]): ToProperties.Aux[In, A, O, F] = form

  implicit def empty[In, F <: Singleton with Poly1]: ToProperties.Aux[In, ∅, ∅, F] = new ToProperties[In, ∅] {
      type Out = ∅
      type Fun = F
      def apply(in: In, a: ∅): Out = ∅
    }

  implicit def cons[
    In,
    AH <: Singleton with AnyProperty, AT <: AnyTypeSet,
    RH <: Tagged[AH], RT <: AnyTypeSet,
    F <: Singleton with Poly1
  ](implicit
    f: Case1.Aux[F, (In, AH), RH], 
    t: ToProperties.Aux[In, AT, RT, F]
  ): ToProperties.Aux[In, AH :~: AT, RH :~: RT, F] =
    new  ToProperties[In, AH :~: AT] {
      
      type Out = RH :~: RT
      type Fun = F
      def apply(in: In, a: AH :~: AT): Out = f((in, a.head)) :~: t(in, a.tail)
    }
}

//////////////////////////////////////////////

trait ToItem[In, I <: AnyItem] {

  type Out = Tagged[I]
  type Fun <: Singleton with Poly1
  def apply(in: In, i: I): Out
}

object ToItem {

  type Aux[In, I <: AnyItem, F <: Singleton with Poly1] = ToItem[In, I] { type Fun = F }

  implicit def buah[In, I <: AnyItem, F <: Singleton with Poly1, Out](implicit 
    fr: ToProperties.Aux[In, I#Properties, Tagged[I], F]
  ): ToItem.Aux[In, I, F] =

      new ToItem[In, I] {

        type Fun = F

        def apply(in: In, i: I): Tagged[I] = {

          val rec = i
          val props = rec.properties
          val itemV = fr(in, props)

          ((i:I) =>> itemV)
        }
      }
}

object From {

  type Item[I <: AnyItem, Out] = FromProperties[I#Properties, Out] { type Reps = I#Raw }

  type ItemAux[I <: AnyItem, F <: Poly1, Out] = 
  
    FromProperties[I#Properties, Out] { 

      type Reps = I#Raw
      type Fun = F
    }
}
