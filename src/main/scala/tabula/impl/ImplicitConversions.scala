package ohnosequences.tabula.impl

import ohnosequences.typesets._
import ohnosequences.scarph._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition, ConditionalOperator}
import com.amazonaws.services.dynamodbv2.model.{Condition => SDKCondition}
import scala.reflect._
import shapeless._, poly._

object ImplicitConversions {

  /* Conversions between item and the SDK representation */
  type SDKRep = Map[String, AttributeValue]
  type SDKElem = (String, AttributeValue)

  implicit val ListLikeSDKRep = new ListLike[SDKRep] {
    type E = SDKElem

    val nil: SDKRep = Map()
    def cons(h: E, t: SDKRep): SDKRep = Map(h) ++ t

    def head(m: SDKRep): E = m.head
    def tail(m: SDKRep): SDKRep = m.tail
  }

  case object toSDKRep extends Poly1 {
    implicit def default[A <: AnyProperty, R <: A#Raw] = 
      at[(A, R)] { case (a, r) => (a.label, getAttrVal(r)) }
  }

  case object fromSDKRep extends Poly1 {
    implicit def caseN[A <: Singleton with AnyProperty.ofType[Num]] = 
      at[(SDKRep, A)]{ case (m, a) => (a ->> m(a.label).getN.toInt): A#Rep }
    implicit def caseS[A <: Singleton with AnyProperty.ofType[String]] = 
      at[(SDKRep, A)]{ case (m, a) => (a ->> m(a.label).getS.toString): A#Rep }
    // TODO: a case for Bytes
  }

  trait SDKRepParser extends AnyTableItemAction {
    val parseSDKRep: SDKRep => item.Rep
  }

  trait SDKRepGetter extends AnyTableItemAction {
    val getSDKRep: item.Rep => SDKRep
  }
  // abstract class SDKRepGetter[A <: AnyTableItemAction] {
  //   type Action = A
  //   val action: Action
  //   def getSDKRep(rep: action.item.Rep): SDKRep
  // }

  // implicit def autoSDKGetter[A <: AnyTableItemAction](a: A)(implicit transf: FromProperties.Item[a.Item, SDKRep]):
  //   SDKRepGetter[A] = new SDKRepGetter[A] {
  //     val action = a
  //     def getSDKRep(rep: a.item.Rep): SDKRep = transf(rep)
  //   }


  /* Properties-related conversions */
  implicit def getAttrDef[A <: AnyProperty](attr: A): AttributeDefinition = {
    val attrDef = new AttributeDefinition().withAttributeName(attr.label)

    attr.classTag.runtimeClass.asInstanceOf[Class[attr.Raw]] match {
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
  implicit def toSDKCondition[C <: Condition](cond: C): SDKCondition = {
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
      case p: AnySimplePredicate => (ConditionalOperator.AND, Map(p.head.property.label -> toSDKCondition(p.head)))
      case p: AnyAndPredicate => (ConditionalOperator.AND, 
                                  toSDKPredicate(p.body)._2 + (p.head.property.label -> toSDKCondition(p.head)))
      case p:  AnyOrPredicate => (ConditionalOperator.OR,
                                  toSDKPredicate(p.body)._2 + (p.head.property.label -> toSDKCondition(p.head)))
    }
  }
    
}
