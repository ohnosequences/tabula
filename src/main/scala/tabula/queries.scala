package ohnosequences.tabula

trait Condition {
  
  type Attribute <: Singleton with AnyAttribute
  val attribute: Attribute
}
/*
  for comparing against values of the corresponding attribute.

  See http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithItems.html#Conditions.SimpleConditions
*/
sealed trait NullaryCondition[A <: Singleton with AnyAttribute] extends Condition {

  type Attribute = A
}
case class has[A <: Singleton with AnyAttribute](val attribute: A) extends NullaryCondition[A] {}
case class hasNot[A <: Singleton with AnyAttribute](val attribute: A) extends NullaryCondition[A] {}

trait SimpleCondition[A <: Singleton with AnyAttribute] extends Condition {

  type Attribute = A
  val value: A#Raw
}

case class NE[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)
  extends SimpleCondition[A]
case class EQ[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)
  extends SimpleCondition[A]
// less than, less than or eq, etc
case class LT[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]
case class LE[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]
case class GT[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]
case class GE[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]

// contains etc
case class BEGINS_WITH[A <: Singleton with AnyAttribute](val attribute: A, val value: A#Raw)(implicit ev: oneOf[ValuesWithPrefixes]#is[A#Raw])
  extends SimpleCondition[A]
case class BETWEEN[A <: Singleton with AnyAttribute](val attribute: A, val start: A#Raw, val end: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]) extends Condition { type Attribute = A }

case class ConditionOps[A <: Singleton with AnyAttribute](attribute: A) {

  def ===(value: A#Raw): EQ[A] = EQ(attribute, value)
  def <(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): LT[A] = LT(attribute, value)
  def ≤(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): LE[A] = LE(attribute, value)
  def >(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): GT[A] = GT(attribute, value)
  def ≥(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): GE[A] = GE(attribute, value)

  def isThere = has(attribute)
  def notThere = hasNot(attribute)
}

object Condition {

  implicit def conditionOps[A <: Singleton with AnyAttribute](attribute: A): ConditionOps[A] = ConditionOps(attribute)
}
