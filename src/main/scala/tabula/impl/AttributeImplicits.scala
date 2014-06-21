package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition}


object AttributeImplicits {
  // TODO: add check for PrimaryKeyValues 
  implicit def getAttrDef[A <: AnyAttribute](attr: A): AttributeDefinition = {
    import scala.reflect._
    val clazz = attr.ctag.runtimeClass.asInstanceOf[Class[attr.Raw]]

    val cInt    = classOf[Int]
    val cString = classOf[String]
    val cBytes  = classOf[Bytes]

    val attrDef = new AttributeDefinition().withAttributeName(attr.label)

    clazz match {
      case c if c == classOf[Int]    => attrDef.withAttributeType(ScalarAttributeType.N)
      case c if c == classOf[String] => attrDef.withAttributeType(ScalarAttributeType.S)
      case c if c == classOf[Bytes]  => attrDef.withAttributeType(ScalarAttributeType.B)
    }
  }

  // TODO: limit T
  implicit def getAttrVal[T](attr: T): AttributeValue = {
    attr match {
      case _: Int    => new AttributeValue().withN(attr.toString)
      case _: String => new AttributeValue().withS(attr.toString)
      // FIXME: some bytes conversion
      // case _: Bytes  => new AttributeValue().withB(attr)
    }
  }

}
