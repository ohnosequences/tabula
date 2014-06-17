package ohnosequences.tabula.impl

import com.amazonaws.auth._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.{Region, Regions}
import java.io.File
import ohnosequences.tabula.Account




case object IrishDynamoDBService extends AnyDynamoDBService {

  override type Account = ohnosequences.tabula.Account
  override type Region = EU.type

  override def endpoint: String = "" //shouldn't be here

  override val account: Account = Account("", "")

  override val region = EU
}


