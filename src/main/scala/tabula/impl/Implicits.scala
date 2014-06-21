package ohnosequences.tabula.impl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition}


object Implicits {
  implicit val defaultDynamoDBClient = new DynamoDBClient(EU,
    new AmazonDynamoDBClient(CredentialProviderChains.default)) {
    client.setRegion(Region.getRegion(Regions.EU_WEST_1))
  }

  import scala.reflect._
  def getAttrDef[A <: AnyAttribute](attr: A)
    // (implicit c: ClassTag[A#Raw])
    : AttributeDefinition = {
      val clazz = attr.ctag.runtimeClass.asInstanceOf[Class[attr.Raw]]

      val cInt    = classOf[Int]
      val cString = classOf[String]
      val cBytes  = classOf[Bytes]

      val attrDef = new AttributeDefinition().withAttributeName(attr.label)

      // FIXME: this gives strange warnings
      clazz match {
        case cInt => attrDef.withAttributeType(ScalarAttributeType.N)
        case cString => attrDef.withAttributeType(ScalarAttributeType.S)
        case cBytes => attrDef.withAttributeType(ScalarAttributeType.B)
      }
    }

  implicit def getAttributeDefinitionN(attr: Attribute[Int])
  // (implicit c: ClassTag[A#Raw])
    : AttributeDefinition = {
    // val clazz = c.runtimeClass.asInstanceOf[Class[p.Raw]]
    new AttributeDefinition()
      .withAttributeName(attr.label)
      .withAttributeType(ScalarAttributeType.N)
  }

  implicit def getAttributeDefinitionS(attr: Attribute[String]): 
        AttributeDefinition = {
    new AttributeDefinition()
      .withAttributeName(attr.label)
      .withAttributeType(ScalarAttributeType.S)
  }

  implicit def getAttributeValue(attr: Int): AttributeValue = {
    new AttributeValue().withN(attr.toString)
  }

  implicit def getAttributeValueS(attr: String): AttributeValue = {
    new AttributeValue().withS(attr.toString)
  }
}
