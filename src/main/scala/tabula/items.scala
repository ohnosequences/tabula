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

    def get[A <: Singleton with AnyAttribute](a: A)
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


// // Some experiments with getting record keys (copied from shapeless) 

// import shapeless._

// trait Keys[S <: TypeSet] extends DepFn1[S] { type Out <: TypeSet }

// object Keys {
//   def apply[S <: TypeSet](implicit keys: Keys[S]): Aux[S, keys.Out] = keys

//   type Aux[S <: TypeSet, O <: TypeSet] = Keys[S] { type Out = O }

//   implicit val empty: Aux[∅, ∅] =
//     new Keys[∅] {
//       type Out = ∅
//       def apply(s: ∅): Out = ∅
//     }

//   import AnyDenotation._
//   implicit def cons[H <: AnyTag, T <: TypeSet]
//     (implicit wk: Witness.Aux[H#Denotation], t: Keys[T]): Aux[H :~: T, H#Denotation :~: t.Out] =
//       new Keys[H :~: T] {
//         type Out = H#Denotation :~: t.Out
//         def apply(s: H :~: T): Out = wk.value :~: t(s.tail)
//       }
// }
