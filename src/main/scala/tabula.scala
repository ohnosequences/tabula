package ohnosequences

import shapeless._

package object tabula {
  
  trait OneOfAux {

    type or[S] <: OneOfAux
    type apply
  }
  // need to add NotIn based on sum type bounds
  trait OneOf[T] extends OneOfAux {  
    type or[S] = OneOf[T with ¬[S]]
    type apply = ¬[T]
  }

  // for convenience
  trait Is[T] extends OneOf[¬[T]]

  type oneOf[W <: OneOfAux] = {
    type λ[X]  = ¬¬[X] <:< W#apply
    type is[X] = ¬¬[X] <:< W#apply
  }

  // stupid alias
  type either[T] = Is[T]

  type Bytes = Seq[Byte]
  type Num   = Int
  type ValidValues = either[Num]#or[String]#or[Bytes]#or[Set[Num]]#or[Set[String]]#or[Set[Bytes]]
  // not documented; the API informs you about it if you try not to adhere to it
  type PrimaryKeyValues = either[String]#or[Num]

  trait AnyAccount {
  
    val id: String
    val canonical_id: String
  }
}