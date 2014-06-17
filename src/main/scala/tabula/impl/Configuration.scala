package ohnosequences.tabula.impl

import com.amazonaws.auth.{PropertiesFileCredentialsProvider, EnvironmentVariableCredentialsProvider, InstanceProfileCredentialsProvider, AWSCredentialsProviderChain}
import java.io.File
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{ScalarAttributeType, AttributeDefinition}

trait AnyDynamoDBClient {
  type Region <: AnyRegion
  val  region: Region

  val client: AmazonDynamoDBClient
}

class DynamoDBClient[R <: AnyRegion](val region: R, val client: AmazonDynamoDBClient) 
  extends AnyDynamoDBClient { type Region = R }

object AnyDynamoDBClient {
  type inRegion[R <: AnyRegion] = AnyDynamoDBClient { type Region = R }
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



