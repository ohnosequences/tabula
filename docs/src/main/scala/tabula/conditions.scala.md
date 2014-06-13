
```scala
package ohnosequences.tabula

import ohnosequences.typesets._

trait Condition {
  
  type Attribute <: Singleton with AnyAttribute
  val attribute: Attribute
}
```


for comparing against values of the corresponding attribute.

See 

- http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithItems.html#Conditions.SimpleConditions
- http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Condition.html

EQ | NE | LE | LT | GE | GT | NOT_NULL | NULL | CONTAINS | NOT_CONTAINS | BEGINS_WITH | IN | BETWEEN


```scala
sealed trait NullaryCondition[A <: Singleton with AnyAttribute] extends Condition {

  type Attribute = A
}
case class NOT_NULL[A <: Singleton with AnyAttribute](val attribute: A) extends NullaryCondition[A] {}
case class NULL[A <: Singleton with AnyAttribute](val attribute: A) extends NullaryCondition[A] {}

trait SimpleCondition[A <: Singleton with AnyAttribute] extends Condition {

  type Attribute = A
  val value: A#Raw
}

case class EQ[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
) extends SimpleCondition[A]

case class NE[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
) extends SimpleCondition[A]

case class LE[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit 
  ev: oneOf[NotSetValues]#is[A#Raw]
) extends SimpleCondition[A]

// less than, less than or eq, etc
case class LT[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit ev: oneOf[NotSetValues]#is[A#Raw])
  extends SimpleCondition[A]

case class GE[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit
  ev: oneOf[NotSetValues]#is[A#Raw]
) extends SimpleCondition[A]

case class GT[A <: Singleton with AnyAttribute](
  val attribute: A,
  val value: A#Raw
)(implicit 
  ev: oneOf[NotSetValues]#is[A#Raw]
) extends SimpleCondition[A]


// contains etc
// note that the value here is a set!
case class CONTAINS[A <: Singleton with AnyAttribute, V](
  val attribute: A, 
  val value: V
)(implicit 
  ev: oneOf[SetValues]#is[A#Raw],
  eq: Set[V] =:= A#Raw
) extends Condition { type Attribute = A }

case class NOT_CONTAINS[A <: Singleton with AnyAttribute, V](
  val attribute: A, 
  val value: V
)(implicit 
  ev: oneOf[SetValues]#is[A#Raw],
  eq: Set[V] =:= A#Raw
) extends Condition { type Attribute = A }

case class BEGINS_WITH[A <: Singleton with AnyAttribute](
  val attribute: A, 
  val value: A#Raw
)(implicit 
  ev: oneOf[ValuesWithPrefixes]#is[A#Raw]
) extends SimpleCondition[A]

case class BETWEEN[A <: Singleton with AnyAttribute](
  val attribute: A,
  val start: A#Raw,
  val end: A#Raw
)(implicit
  ev: oneOf[NotSetValues]#is[A#Raw]
) extends Condition { type Attribute = A }

case class NOT_BETWEEN[A <: Singleton with AnyAttribute](
  val attribute: A,
  val start: A#Raw,
  val end: A#Raw
)(implicit
  ev: oneOf[NotSetValues]#is[A#Raw]
) extends Condition { type Attribute = A }



case class ConditionOps[A <: Singleton with AnyAttribute](attribute: A) {

  final def ===(value: A#Raw): EQ[A] = EQ(attribute, value)
  final def <(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): LT[A] = LT(attribute, value)
  final def ≤(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): LE[A] = LE(attribute, value)
  final def >(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): GT[A] = GT(attribute, value)
  final def ≥(value: A#Raw)(implicit ev: oneOf[NotSetValues]#is[A#Raw]): GE[A] = GE(attribute, value)

  final def ∋[V](value: V)(implicit 
    ev: oneOf[SetValues]#is[A#Raw],
    eq: Set[V] =:= A#Raw
  ): CONTAINS[A,V] = CONTAINS(attribute, value)

  final def ∌[V](value: V)(implicit 
    ev: oneOf[SetValues]#is[A#Raw],
    eq: Set[V] =:= A#Raw
  ): NOT_CONTAINS[A,V] = NOT_CONTAINS(attribute, value)

  final def isThere = NOT_NULL(attribute)
  final def notThere = NULL(attribute)

  final def between(
    start: A#Raw,
    end: A#Raw
  )(implicit 
    ev: oneOf[NotSetValues]#is[A#Raw]
  ): BETWEEN[A] = BETWEEN(attribute, start, end)

  final def notBetween(
    start: A#Raw,
    end: A#Raw
  )(implicit 
    ev: oneOf[NotSetValues]#is[A#Raw]
  ): NOT_BETWEEN[A] = NOT_BETWEEN(attribute, start, end)
}

object Condition {

  implicit def conditionOps[A <: Singleton with AnyAttribute](attribute: A): ConditionOps[A] = ConditionOps(attribute)

  // implicit def toSet[A <: Singleton with AnyAttribute, V](v: V)(implicit eq: Set[V] =:= A#Raw): A#Raw = eq(Set[V](v))
}

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]

[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula/attributes.scala]: attributes.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/conditions.scala]: conditions.scala.md