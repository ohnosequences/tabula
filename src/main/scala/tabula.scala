package ohnosequences

import ohnosequences.cosas._, types._, typeUnions._
// import ohnosequences.cosas.ops.typeSets.TypePredicate

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
