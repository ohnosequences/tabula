package ohnosequences.tabula

case object predicates {

  import conditions._, items._
  import ohnosequences.cosas._, typeSets._

  /*
    ## Predicates

    Predicates represent expressions combining several conditions for a particular item.
    You can combine conditions **either** by `OR` or by `AND` conditional operator (_you can't mix them_).
    Predicate constructors check that the item has the property used in the applied condition.
  */
  trait AnyPredicate {

    type Body <: AnyPredicate
    val  body: Body

    type Head <: AnyCondition
    val  head: Head

    type Item <: AnyItem
    val  item: Item
  }

  /*
    ### OR Predicates
  */
  trait AnyOrPredicate extends AnyPredicate {

    type Body <: AnyOrPredicate

    def or[Other <: AnyCondition](other: Other)(implicit
      ev: Other#Attribute ∈ Body#Item#Attributes
    ): OR[Body, Other] =
       OR(body, other)
  }

  case class OR[B <: AnyOrPredicate, H <: AnyCondition]
    (val body : B,  val head : H) extends AnyOrPredicate {
    type Body = B; type Head = H

    type Item = Body#Item
    val  item = body.item
  }


  /*
    ### AND Predicates
  */
  trait AnyAndPredicate extends AnyPredicate {

    type Body <: AnyAndPredicate

    def and[Other <: AnyCondition](other: Other)(implicit
      ev: Other#Attribute ∈ Body#Item#Attributes
    ): AND[Body, Other] =
       AND(body, other)
  }

  case class AND[B <: AnyAndPredicate, H <: AnyCondition]
    (val body : B,  val head : H) extends AnyAndPredicate {

    type Body = B; type Head = H

    type Item = Body#Item
    val  item = body.item
  }


  /*
    ### Simple Predicates

    It contains only one condition and can be extended either to `OR` or `AND` predicate
  */
  trait AnySimplePredicate extends AnyOrPredicate with AnyAndPredicate {

    type Me = this.type
    type Body = Me
    val  body = this: Me
  }

  case class SimplePredicate[I <: AnyItem, C <: AnyCondition]
    (val item : I,  val head : C) extends AnySimplePredicate {
    type Item = I; type Head = C
  }


  object AnyPredicate {

    type HeadedBy[C <: AnyCondition] = AnyPredicate { type Head <: C }
    type On[I <: AnyItem] = AnyPredicate { type Item = I }
  }

  /* With this you can write `item ? condition` which means `SimplePredicate(item, condition)` */
  implicit def itemPredicateOps[I <: AnyItem](item: I): ItemPredicateOps[I] = ItemPredicateOps(item)
  case class   ItemPredicateOps[I <: AnyItem](val item: I) {
    def ?[C <: AnyCondition](c: C)(implicit
        ev: C#Attribute ∈ I#Attributes
      ): SimplePredicate[I, C] = SimplePredicate(item, c)
  }

  /* An implicit check that this predicate consists only of KeyConditions */
  sealed class OnlyWitnKeyConditions[P <: AnyPredicate]

  object OnlyWitnKeyConditions extends OnlyWitnKeyConditions2 {
    implicit def simple[P <: AnySimplePredicate with AnyPredicate.HeadedBy[AnyKeyCondition]]:
      OnlyWitnKeyConditions[P] = new OnlyWitnKeyConditions[P]
  }

  // This needs to be separated, otherwise implicits will deverge for SimplePredicate
  trait OnlyWitnKeyConditions2 {

    implicit def and[P <: AnyAndPredicate with AnyPredicate.HeadedBy[AnyKeyCondition]]
      (implicit ev: OnlyWitnKeyConditions[P#Body]):
                    OnlyWitnKeyConditions[P] =
                new OnlyWitnKeyConditions[P]

    implicit def  or[P <: AnyOrPredicate  with AnyPredicate.HeadedBy[AnyKeyCondition]]
      (implicit ev: OnlyWitnKeyConditions[P#Body]):
                    OnlyWitnKeyConditions[P] =
                new OnlyWitnKeyConditions[P]
  }

}
