package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._

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

  // NOTE: this is for compatibility with scarph, but I think it's not needed
  // you can always say instead: `(implicit e: attribute.type ∈ item.Attributes`
  implicit def itemAttributeOps[I <: AnyItem, A <: AnyAttribute]
    (implicit e: A ∈ I#Attributes): I HasProperty A = new (I HasProperty A)
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


// Some experiments with getting record keys (copied from shapeless) 

import shapeless._, poly._

trait Keys[S <: TypeSet] extends DepFn1[S] { type Out <: TypeSet }

object Keys {
  def apply[S <: TypeSet](implicit keys: Keys[S]): Aux[S, keys.Out] = keys

  type Aux[S <: TypeSet, O <: TypeSet] = Keys[S] { type Out = O }

  implicit val empty: Aux[∅, ∅] =
    new Keys[∅] {
      type Out = ∅
      def apply(s: ∅): Out = ∅
    }

  import AnyDenotation._
  implicit def cons[H <: AnyTag, T <: TypeSet]
    (implicit wk: Witness.Aux[H#Denotation], t: Keys[T]): Aux[H :~: T, H#Denotation :~: t.Out] =
      new Keys[H :~: T] {
        type Out = H#Denotation :~: t.Out
        def apply(s: H :~: T): Out = wk.value :~: t(s.tail)
      }
}

import AnyDenotation._
trait GetTag[S] extends DepFn1[S] { type Out }

object GetTag {
  def apply[S](implicit gt: GetTag[S]): Aux[S, gt.Out] = gt

  type Aux[S, O] = GetTag[S] { type Out = O }

  implicit def cons[R, D]
    (implicit ks: Keys.Aux[R :~: ∅, D :~: ∅]): Aux[R, D] =
      new GetTag[R] {
        type Out = D
        def apply(s: R): Out = ks(s :~: ∅).head
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

  type R <: TypeSet             // representation of attributes
  type F <: Singleton with Poly // transformation function

  def apply(a: A, r: R): Out
}

object FromAttributes {
  def apply[A <: TypeSet, R <: TypeSet, F <: Singleton with Poly, Out](implicit tr: FromAttributes.Aux[A, R, F, Out]):
    FromAttributes.Aux[A, R, F, Out] = tr

  type Aux[A <: TypeSet, R0 <: TypeSet, F0 <: Singleton with Poly, Out] =
    FromAttributes[A, Out] { 
      type R = R0
      type F = F0
    }

  type Anyhow[A <: TypeSet, R0 <: TypeSet, Out] =
    FromAttributes[A, Out] { 
      type R = R0
    }

  implicit def empty[Out, F0 <: Singleton with Poly]
    (implicit m: ListLike[Out]): FromAttributes.Aux[∅, ∅, F0, Out] = new FromAttributes[∅, Out] {
      type R = ∅
      type F = F0
      def apply(i: ∅, r: ∅): Out = m.nil
    }

  implicit def cons[
    F0 <: Singleton with Poly,
    AH <: Singleton with AnyAttribute, AT <: TypeSet,
    RH <: AH#Raw, RT <: TypeSet,
    E, Out
  ](implicit
    m: ListLike.Of[Out, E], 
    f: Case1.Aux[F0, (AH, RH), E], 
    t: FromAttributes.Aux[AT, RT, F0, Out]
  ): FromAttributes.Aux[AH :~: AT, RH :~: RT, F0, Out] =
    new FromAttributes[AH :~: AT, Out] {
      type R = RH :~: RT
      type F = F0
      def apply(a: AH :~: AT, r: RH :~: RT): Out = {
        m.cons(
          f((a.head, r.head)),
          t(a.tail, r.tail)
        )
      }
    }
}

trait FromItem[I <: Singleton with AnyItem, Out] {
  type R = I#Rep
  type F <: Singleton with Poly
  def apply(i: I, r: R): Out
}

object FromItem {
  type Aux[I <: Singleton with AnyItem, F0 <: Singleton with Poly, Out] = FromItem[I, Out] { type F = F0 }

  implicit def yeah[I <: Singleton with AnyItem, F0 <: Singleton with Poly, Out]
    (implicit tr: FromAttributes.Aux[I#Attributes, I#Raw, F0, Out]): FromItem.Aux[I, F0, Out] =
      new FromItem[I, Out] {
        type F = F0
        def apply(i: I, r: R): Out = tr(i.attributes, r)
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
