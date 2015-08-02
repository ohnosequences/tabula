package ohnosequences.tabula.impl

import ohnosequences.cosas._, types._, properties._
import ohnosequences.tabula._, attributes._, conditions._, predicates._

import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition, ConditionalOperator}
import com.amazonaws.services.dynamodbv2.model.{Condition => SDKCondition}
import scala.reflect._
import shapeless._, poly._
import spire.algebra.Monoid


object ImplicitConversions {

  /* Conversions between item and the SDK representation */
  type SDKRep = Map[String, AttributeValue]
  type SDKElem = (String, AttributeValue)

  import ohnosequences.cosas.ops.typeSets._

  implicit val SDKRepMonoid: Monoid[SDKRep] = new Monoid[SDKRep] {

    def id: SDKRep = Map[String, AttributeValue]()
    def op(x: SDKRep, y: SDKRep): SDKRep = x ++ y
  }

  object SDKRepParsers {

    implicit def caseNum[A <: AnyAttribute.ofType[Num]](attr: A, rep: SDKRep):
      (ValueOf[A], SDKRep) = (attr(rep(attr.label).getN.toInt), rep)

    implicit def caseString[A <: AnyAttribute.ofType[String]](attr: A, rep: SDKRep):
      (ValueOf[A], SDKRep) = (attr(rep(attr.label).getS.toString), rep)

    // TODO: a case for Bytes
  }

  object SDKRepSerializers {

    implicit def default[A <: AnyAttribute](t: ValueOf[A])
      (implicit getP: ValueOf[A] => A): SDKRep = Map(getP(t).label -> getAttrVal[A#Raw](t.value))
  }

  // trait SDKRepParser extends AnyItemAction {
  //   val parseSDKRep: SDKRep => ValueOf[Item]
  // }

  // trait SDKRepGetter extends AnyItemAction {
  //   val getSDKRep: Item#Raw => SDKRep
  // }
  // trait SDKRepGetter[A <: AnyItemAction] {
  //   def getSDKRep(rep: ValueOf[A#Item]): SDKRep
  // }

  // implicit def autoSDKGetter[A <: AnyItemAction](a: A)(implicit transf: FromProperties.Item[a.Item, SDKRep]):
  //   SDKRepGetter[A] = new SDKRepGetter[A] {
  //     val action = a
  //     def getSDKRep(rep: a.item.Rep): SDKRep = transf(rep)
  //   }


  /* Properties-related conversions */
  implicit def getAttrDef[A <: AnyAttribute](attr: A): AttributeDefinition = {

    val attrDef = new AttributeDefinition().withAttributeName(attr.label)

    attr.rawTag.runtimeClass.asInstanceOf[Class[A#Raw]] match {
      case c if c == classOf[Num]    => attrDef.withAttributeType(ScalarAttributeType.N)
      case c if c == classOf[String] => attrDef.withAttributeType(ScalarAttributeType.S)
      case c if c == classOf[Bytes]  => attrDef.withAttributeType(ScalarAttributeType.B)
    }
  }

  // FIXME: restrict T somehow, maybe Typeable instance is needed
  implicit def getAttrVal[T] // : Typeable] // : oneOf[NotSetValues]#is]
    (attr: T): AttributeValue = {

    // val B = TypeCase[Bytes]
    attr match {
      case _: Num    => new AttributeValue().withN(attr.toString)
      case _: String => new AttributeValue().withS(attr.toString)
      // TODO: test the Bytes case
      case a: Bytes => {
        import java.nio._
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(a.length)
        byteBuffer.put(Array[Byte](a: _*))
        new AttributeValue().withB(byteBuffer)
      }
    }
  }

  /* Conditions-related conversions */
  implicit def toSDKCondition[C <: AnyCondition](cond: C): SDKCondition = {
    import scala.collection.JavaConversions._

    val sdkCond = new SDKCondition().withComparisonOperator(cond.getClass.getSimpleName)

    cond match {
      case c: NullaryCondition[_] => sdkCond
      case _ => {
        val attrValList: java.util.Collection[AttributeValue] = (cond match {
          case c:  SimpleCondition[_] => List(c.value)
          case c:      CONTAINS[_, _] => List(c.value)
          case c:  NOT_CONTAINS[_, _] => List(c.value)
          case c:          BETWEEN[_] => List(c.start, c.end)
          case c:               IN[_] => c.values
        }) map getAttrVal

        sdkCond.withAttributeValueList(attrValList)
      }
    }
  }

  implicit def toSDKPredicate[P <: AnyPredicate](pred: P): (ConditionalOperator, Map[String, SDKCondition]) = {

    pred match {
      case p: AnySimplePredicate => (ConditionalOperator.AND, Map(p.head.attribute.label -> toSDKCondition(p.head)))
      case p: AnyAndPredicate => (ConditionalOperator.AND,
                                  toSDKPredicate(p.body)._2 + (p.head.attribute.label -> toSDKCondition(p.head)))
      case p:  AnyOrPredicate => (ConditionalOperator.OR,
                                  toSDKPredicate(p.body)._2 + (p.head.attribute.label -> toSDKCondition(p.head)))
    }
  }

}
