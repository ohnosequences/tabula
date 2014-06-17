package ohnosequences.tabula.impl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.{Regions, Region}
import ohnosequences.tabula.Attribute
import com.amazonaws.services.dynamodbv2.model.{ScalarAttributeType, AttributeDefinition}


object Implicits {
  implicit object DefaultDynamoDBClient extends DynamoDBClient {
    val client = new AmazonDynamoDBClient(CredentialProviderChains.default)
    client.setRegion(Region.getRegion(Regions.EU_WEST_1))
  }

  implicit def getAttributeDefinition(attr: Attribute[Int]): AttributeDefinition = {
    new AttributeDefinition()
      .withAttributeName(attr.label)
      .withAttributeType(ScalarAttributeType.N)
  }
}