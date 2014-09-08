package ohnosequences

import ohnosequences.pointless._, AnyType._, AnyTypeUnion._
import ohnosequences.pointless.ops.typeSet.TypePredicate

// in package object only type-aliases
package object tabula {
  
  type Bytes = Array[Byte]
  type Num   = Integer
  // not documented; the API informs you about it if you try not to adhere to it
  type NotSetValues = either[Num]#or[String]#or[Bytes]
  type SetValues = either[Set[Num]]#or[Set[String]]#or[Set[Bytes]]

  type PrimaryKeyValues = NotSetValues
  type ValidValues = NotSetValues#or[Set[Num]]#or[Set[String]]#or[Set[Bytes]]
  type ValuesWithPrefixes = either[String]#or[Bytes]

}
