package ohnosequences.tabula.impl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{ScalarAttributeType, AttributeDefinition}


object Implicits {
  implicit val DefaultDynamoDBClient = new DynamoDBClient(EU, 
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
}
