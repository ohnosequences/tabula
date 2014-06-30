package ohnosequences.tabula.impl

import ohnosequences.typesets._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition}
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
    implicit def default[A <: AnyAttribute, R <: A#Raw] = 
      at[(A, R)] { case (a, r) => (a.label, getAttrVal(r)) }
  }

  case object fromSDKRep extends Poly1 {
    implicit def caseN[A <: Singleton with AnyAttribute.With[Int]] = 
      at[(SDKRep, A)]{ case (m, a) => (a ->> m(a.label).getN.toInt): A#Rep }
    implicit def caseS[A <: Singleton with AnyAttribute.With[String]] = 
      at[(SDKRep, A)]{ case (m, a) => (a ->> m(a.label).getS.toString): A#Rep }
    // TODO: a case for Bytes
  }

  trait SDKRepParser extends AnyTableItemAction {
    val parseSDKRep: SDKRep => item.Rep
  }

  trait SDKRepGetter extends AnyTableItemAction {
    val getSDKRep: item.Rep => SDKRep
  }


  /* Attributes-related conversions */
  implicit def getAttrDef[A <: AnyAttribute](attr: A): AttributeDefinition = {
    val attrDef = new AttributeDefinition().withAttributeName(attr.label)

    attr.classTag.runtimeClass.asInstanceOf[Class[attr.Raw]] match {
      case c if c == classOf[Int]    => attrDef.withAttributeType(ScalarAttributeType.N)
      case c if c == classOf[String] => attrDef.withAttributeType(ScalarAttributeType.S)
      case c if c == classOf[Bytes]  => attrDef.withAttributeType(ScalarAttributeType.B)
    }
  }

  // FIXME: restrict T somehow, maybe Typeable instance is needed
  implicit def getAttrVal[T] // : Typeable] // : oneOf[NotSetValues]#is]
    (attr: T): AttributeValue = {

    // val B = TypeCase[Bytes]
    attr match {
      case _: Int    => new AttributeValue().withN(attr.toString)
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

}
