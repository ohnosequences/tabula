package ohnosequences.tabula

trait Condition {
  
  type Attribute <: AnyAttribute
  val attribute: Attribute
}
/*
  for comparing against values of the corresponding attribute.

  See http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithItems.html#Conditions.SimpleConditions
*/
trait SimpleCondition[A <: AnyAttribute] extends Condition {

  type Attribute = A
  val value: A#Raw
}

case class NE[A <: AnyAttribute](val attribute: A, val value: A#Raw)
  extends SimpleCondition[A]
case class EQ[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)
  extends SimpleCondition[A]
// less than, less than or eq, etc
case class LT[A <: AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]
case class LE[A <: AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]
case class GT[A <: AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]
case class GE[A <: AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]

// contains etc
case class BEGINS_WITH[A <: AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[ValuesWithPrefixes]#is[A#Raw])
  extends SimpleCondition[A]
case class BETWEEN[A <: AnyAttribute](val attribute: A, val start: A#Raw, val end: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]) extends Condition { type Attribute = A }
