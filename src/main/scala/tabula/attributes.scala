package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import scala.reflect.ClassTag

/* Basically attributes are just another name for properties from scarph */
sealed trait AnyAttribute extends AnyProperty 

/* But their Raw type is restricted */
class Attribute[V : oneOf[ValidValues]#is](implicit classTag: ClassTag[V]) 
  extends Property[V]()(classTag) with AnyAttribute 

object Attribute {

  type SetsOf[V] = AnyAttribute { type Raw = Set[V] }
}
