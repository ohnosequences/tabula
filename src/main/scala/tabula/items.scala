package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import shapeless._, poly._

/*
  ## Items

  This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.
*/

trait AnyItem extends Representable { item =>
  val label: String

  /* The table is accessible through the item type */
  type Table <: AnyTable
  val  table: Table

  /* Any item has a fixed set of attributes */
  type Attributes <: TypeSet
  val  attributes: Attributes
  // should be provided implicitly:
  val  attributesBound: boundedBy[AnyAttribute]#is[Attributes]

  /* Then the raw presentation of the item is kind of a record 
     in which the keys set is exactly the `Attributes` type,
     i.e. it's a set of attributes representations */
  type Raw <: TypeSet
  // should be provided implicitly:
  val  representedAttributes: Represented.By[Attributes, Raw]

  /* This extends representation type by a getter method */
  implicit def attributeOps(rep: item.Rep): AttributeOps = AttributeOps(rep)
  case class   AttributeOps(rep: item.Rep) {

    def attr[A <: Singleton with AnyAttribute](a: A)
      (implicit 
        // isThere: A ∈ item.Attributes, // lookup does this check anyway 
        lookup: Lookup[item.Raw, a.Rep]
      ): a.Rep = lookup(rep)
  }

}

class Item[T <: AnyTable, A <: TypeSet, R <: TypeSet]
  (val table: T, val attributes: A)
  (implicit 
    val representedAttributes: Represented.By[A, R],
    val attributesBound: boundedBy[AnyAttribute]#is[A]
  ) extends AnyItem {

  val label = this.toString

  type Table = T
  type Attributes = A
  type Raw = R
}

object AnyItem {
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
}


/* 
  This is a generic thing for dereriving the set of representations 
  from a set of representable singletons. For example:
  ```scala
  case object id extends Attribute[Int]
  case object name extends Attribute[String]

  implicitly[Represented.By[
    id.type :~: name.type :~: ∅,
    id.Rep  :~: name.Rep  :~: ∅
  ]]
  ```

  See examples of usage it for item attributes in tests
*/
@annotation.implicitNotFound(msg = "Can't construct a set of representations for ${S}")
sealed class Represented[S <: TypeSet] { type Out <: TypeSet }

object Represented {
  type By[S <: TypeSet, O <: TypeSet] = Represented[S] { type Out = O }

  implicit val empty: ∅ By ∅ = new Represented[∅] { type Out = ∅ }

  implicit def cons[H <: Singleton with Representable,  T <: TypeSet]
    (implicit t: Represented[T]): (H :~: T) By (H#Rep :~: t.Out) =
          new Represented[H :~: T] { type Out = H#Rep :~: t.Out }
}


/* Takes a set of Reps and returns the set of what they represent */
trait TagsOf[S <: TypeSet] extends DepFn1[S] { type Out <: TypeSet }

object TagsOf {
  def apply[S <: TypeSet](implicit keys: TagsOf[S]): Aux[S, keys.Out] = keys

  type Aux[S <: TypeSet, O <: TypeSet] = TagsOf[S] { type Out = O }

  implicit val empty: Aux[∅, ∅] =
    new TagsOf[∅] {
      type Out = ∅
      def apply(s: ∅): Out = ∅
    }

  implicit def cons[H <: Singleton with Representable, T <: TypeSet]
    (implicit fromRep: H#Rep => H, t: TagsOf[T]): Aux[H#Rep :~: T, H :~: t.Out] =
      new TagsOf[H#Rep :~: T] {
        type Out = H :~: t.Out
        def apply(s: H#Rep :~: T): Out = fromRep(s.head) :~: t(s.tail)
      }
}

//////////////////////////////////////////////

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

/* Transforms a representation of item to something else */
trait FromAttributes[
    A <: TypeSet, // set of attributes
    Out           // what we want to get
  ] {

  type Reps <: TypeSet            // representation of attributes
  type Fun <: Singleton with Poly // transformation function

  def apply(r: Reps): Out
}

object FromAttributes {
  def apply[A <: TypeSet, Reps <: TypeSet, F <: Singleton with Poly, Out](implicit tr: FromAttributes.Aux[A, Reps, F, Out]):
    FromAttributes.Aux[A, Reps, F, Out] = tr

  type Aux[A <: TypeSet, R <: TypeSet, F <: Singleton with Poly, Out] =
    FromAttributes[A, Out] { 
      type Reps = R
      type Fun = F
    }

  type Anyhow[A <: TypeSet, R <: TypeSet, Out] =
    FromAttributes[A, Out] { 
      type Reps = R
    }

  type Item[I <: AnyItem, Out] = FromAttributes[I#Attributes, Out] { type Reps = I#Raw }
  type ItemAux[I <: AnyItem, F <: Singleton with Poly, Out] = 
    FromAttributes[I#Attributes, Out] { 
      type Reps = I#Raw
      type Fun = F
    }

  implicit def empty[Out, F <: Singleton with Poly]
    (implicit m: ListLike[Out]): FromAttributes.Aux[∅, ∅, F, Out] = new FromAttributes[∅, Out] {
      type Reps = ∅
      type Fun = F
      def apply(r: ∅): Out = m.nil
    }

  implicit def cons[
    F <: Singleton with Poly,
    AH <: Singleton with AnyAttribute, AT <: TypeSet,
    RT <: TypeSet,
    E, Out
  ](implicit
    tagOf: AH#Rep => AH,
    listLike: ListLike.Of[Out, E], 
    transform: Case1.Aux[F, (AH, AH#Rep), E], 
    recOnTail: FromAttributes.Aux[AT, RT, F, Out]
  ): FromAttributes.Aux[AH :~: AT, AH#Rep :~: RT, F, Out] =
    new FromAttributes[AH :~: AT, Out] {
      type Reps = AH#Rep :~: RT
      type Fun = F
      def apply(r: AH#Rep :~: RT): Out = {
        listLike.cons(
          transform((tagOf(r.head), r.head)),
          recOnTail(r.tail)
        )
      }
    }
}

///////////////////////////////////////////////////////////////

/* Transforms attributes set representation from something else */
trait ToAttributes[
    In,          // some other representation
    A <: TypeSet // set of corresponding attributes
  ] {

  type Out <: TypeSet             // representation of attributes
  type Fun <: Singleton with Poly // transformation function

  def apply(in: In, a: A): Out
}

object ToAttributes {
  type Aux[In, A <: TypeSet, O <: TypeSet, F <: Singleton with Poly] = ToAttributes[In, A] { type Out = O; type Fun = F } 

  def apply[In, A <: TypeSet, O <: TypeSet, F <: Singleton with Poly]
    (implicit form: ToAttributes.Aux[In, A, O, F]): ToAttributes.Aux[In, A, O, F] = form

  implicit def empty[In, F <: Singleton with Poly]: ToAttributes.Aux[In, ∅, ∅, F] = new ToAttributes[In, ∅] {
      type Out = ∅
      type Fun = F
      def apply(in: In, a: ∅): Out = ∅
    }

  implicit def cons[
    In,
    AH <: Singleton with AnyAttribute, AT <: TypeSet,
    RH <: AH#Rep, RT <: TypeSet,
    F <: Singleton with Poly
  ](implicit
    f: Case1.Aux[F, (In, AH), RH], 
    t: ToAttributes.Aux[In, AT, RT, F]
  ): ToAttributes.Aux[In, AH :~: AT, RH :~: RT, F] =
    new  ToAttributes[In, AH :~: AT] {
      type Out = RH :~: RT
      type Fun = F
      def apply(in: In, a: AH :~: AT): Out = f((in, a.head)) :~: t(in, a.tail)
    }
}

trait ToItem[In, I <: Singleton with AnyItem] {
  type Out = I#Rep
  type Fun <: Singleton with Poly
  def apply(in: In, i: I): Out
}

object ToItem {
  type Aux[In, I <: Singleton with AnyItem, F <: Singleton with Poly] = ToItem[In, I] { type Fun = F }

  implicit def buah[In, I <: Singleton with AnyItem, F <: Singleton with Poly, Out]
    (implicit fr: ToAttributes.Aux[In, I#Attributes, I#Raw, F]): ToItem.Aux[In, I, F] =
      new ToItem[In, I] {
        type Fun = F
        def apply(in: In, i: I): Out = (i: I) ->> fr(in, i.attributes)
      }
}
