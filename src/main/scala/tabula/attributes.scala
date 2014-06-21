package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import scala.reflect.ClassTag

sealed trait AnyAttribute extends AnyProperty {
  val ctag: ClassTag[Raw]
}
class Attribute[V: oneOf[ValidValues]#is](implicit val ctag: ClassTag[V]) extends Property[V]()(ctag) with AnyAttribute {
}

object Attribute {

  type SetsOf[V] = AnyAttribute { type Raw = Set[V] }
}
