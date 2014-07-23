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

  /* Any item has a fixed set of properties */
  type Properties <: TypeSet
  val  properties: Properties
  // should be provided implicitly:
  val  propertiesBound: Properties << AnyProperty

  /* Then the raw presentation of the item is kind of a record 
     in which the keys set is exactly the `Properties` type,
     i.e. it's a set of properties representations */
  type Raw <: TypeSet
  // should be provided implicitly:
  val  representedProperties: Represented.By[Properties, Raw]
  val  propertiesHaveValidTypes: everyElementOf[Raw]#isOneOf[ValidValues]

  /* This extends representation type by a getter method */
  implicit def propertyOps(rep: item.Rep): PropertyOps = PropertyOps(rep)
  case class   PropertyOps(rep: item.Rep) {

    def get[A <: Singleton with AnyProperty](a: A)
      (implicit 
        isThere: A ∈ item.Properties,
        lookup: Lookup[item.Raw, a.Rep]
      ): a.Rep = lookup(rep)


    def update[A <: Singleton with AnyProperty, S <: TypeSet](arep: A#Rep)
      (implicit 
        isThere: A ∈ item.Properties,
        replace: Replace[item.Raw, (A#Rep :~: ∅)]
      ): item.Rep = item ->> replace(rep, arep :~: ∅)

    def update[As <: TypeSet, S <: TypeSet](as: As)
      (implicit 
        check: As ⊂ item.Raw,
        replace: Replace[item.Raw, As]
      ): item.Rep = item ->> replace(rep, as)


    def as[I <: AnyItem](i: I)(implicit
      project: Choose[item.Raw, i.Raw]
    ): i.Rep = i ->> project(rep)

    def as[I <: AnyItem, Rest <: TypeSet, Uni <: TypeSet, Missing <: TypeSet](i: I, rest: Rest)
      (implicit
        missing: (i.Raw \ item.Raw) { type Out = Missing },
        allMissing: Rest ~ Missing,
        uni: (item.Raw ∪ Rest) { type Out = Uni },
        project: Choose[Uni, i.Raw]
      ): i.Rep = i ->> project(uni(rep, rest))

  }

  /* Same as just tagging with `->>`, but you can pass fields in any order */
  def fields[R <: TypeSet](r: R)(implicit 
    p: R ~> item.Raw
  ): item.Rep = item ->> p(r)

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


/* 
  This is a generic thing for dereriving the set of representations 
  from a set of representable singletons. For example:
  ```scala
  case object id extends Property[Int]
  case object name extends Property[String]

  implicitly[Represented.By[
    id.type :~: name.type :~: ∅,
    id.Rep  :~: name.Rep  :~: ∅
  ]]
  ```

  See examples of usage it for item properties in tests
*/
@annotation.implicitNotFound(msg = "Can't construct a set of representations for ${S}")
sealed class Represented[S <: TypeSet] { type Out <: TypeSet }

object Represented {
  type By[S <: TypeSet, O <: TypeSet] = Represented[S] { type Out = O }

  implicit val empty: ∅ By ∅ = new Represented[∅] { type Out = ∅ }

  implicit def cons[H <: Singleton with Representable, T <: TypeSet]
    (implicit t: Represented[T]): (H :~: T) By (H#Rep :~: t.Out) =
          new Represented[H :~: T] { type Out = H#Rep :~: t.Out }
}


/* Takes a set of Reps and returns the set of what they represent */
import shapeless._, poly._

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
trait FromProperties[
    A <: TypeSet, // set of properties
    Out           // what we want to get
  ] {

  type Reps <: TypeSet            // representation of properties
  type Fun <: Singleton with Poly // transformation function

  def apply(r: Reps): Out
}

object FromProperties {
  def apply[A <: TypeSet, Reps <: TypeSet, F <: Singleton with Poly, Out](implicit tr: FromProperties.Aux[A, Reps, F, Out]):
    FromProperties.Aux[A, Reps, F, Out] = tr

  type Aux[A <: TypeSet, R <: TypeSet, F <: Singleton with Poly, Out] =
    FromProperties[A, Out] { 
      type Reps = R
      type Fun = F
    }

  type Anyhow[A <: TypeSet, R <: TypeSet, Out] =
    FromProperties[A, Out] { 
      type Reps = R
    }

  type Item[I <: AnyItem, Out] = FromProperties[I#Properties, Out] { type Reps = I#Raw }
  type ItemAux[I <: AnyItem, F <: Singleton with Poly, Out] = 
    FromProperties[I#Properties, Out] { 
      type Reps = I#Raw
      type Fun = F
    }

  implicit def empty[Out, F <: Singleton with Poly]
    (implicit m: ListLike[Out]): FromProperties.Aux[∅, ∅, F, Out] = new FromProperties[∅, Out] {
      type Reps = ∅
      type Fun = F
      def apply(r: ∅): Out = m.nil
    }

  implicit def cons[
    F <: Singleton with Poly,
    AH <: Singleton with AnyProperty, AT <: TypeSet,
    RT <: TypeSet,
    E, Out
  ](implicit
    tagOf: AH#Rep => AH,
    listLike: ListLike.Of[Out, E], 
    transform: Case1.Aux[F, (AH, AH#Rep), E], 
    recOnTail: FromProperties.Aux[AT, RT, F, Out]
  ): FromProperties.Aux[AH :~: AT, AH#Rep :~: RT, F, Out] =
    new FromProperties[AH :~: AT, Out] {
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

/* Transforms properties set representation from something else */
trait ToProperties[
    In,          // some other representation
    A <: TypeSet // set of corresponding properties
  ] {

  type Out <: TypeSet             // representation of properties
  type Fun <: Singleton with Poly // transformation function

  def apply(in: In, a: A): Out
}

object ToProperties {
  type Aux[In, A <: TypeSet, O <: TypeSet, F <: Singleton with Poly] = ToProperties[In, A] { type Out = O; type Fun = F } 

  def apply[In, A <: TypeSet, O <: TypeSet, F <: Singleton with Poly]
    (implicit form: ToProperties.Aux[In, A, O, F]): ToProperties.Aux[In, A, O, F] = form

  implicit def empty[In, F <: Singleton with Poly]: ToProperties.Aux[In, ∅, ∅, F] = new ToProperties[In, ∅] {
      type Out = ∅
      type Fun = F
      def apply(in: In, a: ∅): Out = ∅
    }

  implicit def cons[
    In,
    AH <: Singleton with AnyProperty, AT <: TypeSet,
    RH <: AH#Rep, RT <: TypeSet,
    F <: Singleton with Poly
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
