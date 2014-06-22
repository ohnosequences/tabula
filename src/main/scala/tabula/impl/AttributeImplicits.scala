package ohnosequences.tabula.impl

import ohnosequences.typesets._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition}
import scala.reflect._

object AttributeImplicits {
  implicit def getAttrDef[A <: AnyAttribute](attr: A): AttributeDefinition = {
    val attrDef = new AttributeDefinition().withAttributeName(attr.label)

    // NOTE: don't know if the commented code is needed here
    attr.classTag //.runtimeClass.asInstanceOf[Class[attr.Raw]] 
      match {
      case c if c == classOf[Int]    => attrDef.withAttributeType(ScalarAttributeType.N)
      case c if c == classOf[String] => attrDef.withAttributeType(ScalarAttributeType.S)
      case c if c == classOf[Bytes]  => attrDef.withAttributeType(ScalarAttributeType.B)
      // TODO: are sets types needed here?
    }
  }

  // import scala.collection.JavaConversions._
  // FIXME: want to check the type range, but it gives a warning
  implicit def getAttrVal[T] // : oneOf[NotSetValues]#is]
    (attr: T): AttributeValue = {

    attr match {
      case _: Int    => new AttributeValue().withN(attr.toString)
      case _: String => new AttributeValue().withS(attr.toString)
      // FIXME: don't know how to overcome type-erasure warning in this case "/
      case a: Bytes => { 
        import java.nio._
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(a.length)
        byteBuffer.put(Array[Byte](a: _*))
        new AttributeValue().withB(byteBuffer)
      }
    }
  }

}
