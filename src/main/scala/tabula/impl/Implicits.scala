package ohnosequences.tabula.impl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition}


object Implicits {
  implicit def DefaultDynamoDBClient = new DynamoDBClient(EU, 
    new AmazonDynamoDBClient(CredentialProviderChains.default)) {
    client.setRegion(Region.getRegion(Regions.EU_WEST_1))
  }

  // implicit def getAttributeDefinition[A <: AnyAttribute](attr: A)
  implicit def getAttributeDefinition(attr: Attribute[Int])
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

  implicit def getAttributeValue(attr: String): AttributeValue = {
    new AttributeValue().withS(attr.toString)
  }
}
