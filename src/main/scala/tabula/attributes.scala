package ohnosequences.tabula

case object attributes {

  import ohnosequences.cosas._, typeUnions._, properties._
  import scala.reflect.ClassTag


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


  object AnyAttribute {

    type ofType[T] = AnyAttribute { type Raw = T }
  }
}
