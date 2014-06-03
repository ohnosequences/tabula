package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import scala.reflect.ClassTag

sealed trait AnyAttribute extends AnyProperty
class Attribute[V: oneOf[ValidValues]#is](implicit val c0: ClassTag[V]) extends Property[V]() with AnyAttribute {}
