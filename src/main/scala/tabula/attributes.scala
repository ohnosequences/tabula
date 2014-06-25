package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import scala.reflect.ClassTag

/* Basically attributes are just another name for properties from scarph */
sealed trait AnyAttribute extends AnyProperty 

/* But their Raw type is restricted */
class Attribute[V](implicit 
  classTag: ClassTag[V],
  ev: V :<: ValidValues
) extends Property[V]()(classTag) with AnyAttribute

object AnyAttribute {
  type With[V] = AnyAttribute { type Raw = V }
}

object Attribute {
  type Of[I <: AnyItem] = { type is[A <: AnyAttribute] = A ∈ I#Attributes }

  type SetsOf[V] = AnyAttribute { type Raw = Set[V] }
}
