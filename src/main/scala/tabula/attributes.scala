package ohnosequences.tabula

import ohnosequences.cosas._, typeUnions._, properties._
import scala.reflect.ClassTag

case object attributes {

  trait AnyAttribute extends AnyProperty {

    // should be provieded implicitly:
    val rawTag: ClassTag[Raw]
    val validRaw: Raw isOneOf ValidValues
  }

  class Attribute[R](val label: String)
    (implicit
      val rawTag: ClassTag[R],
      val validRaw: R isOneOf ValidValues
    ) extends AnyAttribute { type Raw = R }

}
