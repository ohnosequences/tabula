package ohnosequences.tabula.impl

import com.amazonaws.auth.{PropertiesFileCredentialsProvider, EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider, AWSCredentialsProviderChain}
import java.io.File
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.{Regions, Region}
import ohnosequences.tabula.Attribute
import com.amazonaws.services.dynamodbv2.model.{ScalarAttributeType, AttributeDefinition}

trait DynamoDBClient {
  val client: AmazonDynamoDBClient
}

object DefaultDynamoDBClient extends DynamoDBClient {
  val client = new AmazonDynamoDBClient(CredentialProviderChains.default)
  client.setRegion(Region.getRegion(Regions.EU_WEST_1))
}

trait Credentials {
  val credetialsProvider:  com.amazonaws.auth.AWSCredentialsProvider
}

object CredentialProviderChains {
  val default = new AWSCredentialsProviderChain(
    new InstanceProfileCredentialsProvider(),
    new EnvironmentVariableCredentialsProvider(),
    new PropertiesFileCredentialsProvider(new File(System.getProperty("user.home"), "credentials").getPath)
  )
}



