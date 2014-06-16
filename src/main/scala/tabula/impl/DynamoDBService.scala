package ohnosequences.tabula.impl

import com.amazonaws.auth._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.{Region, Regions}
import java.io.File
import ohnosequences.tabula.Account


//it's wrong
class ReadyToDelete[T <: AnyTable with Singleton](val service: IrishDynamoDBService, table: T) extends Active {
  override val account = service.account
  override val resource: Resource = table
  override val throughputStatus: ThroughputStatus = InitialThroughput(0, 0)
  override type Resource = T
}

trait DeleteTableAux extends AnyDeleteTable {
  override type Input <: AnyTable with Singleton
  val state: InitialState[Input]
  override val input: Input //table
  override type InputState = InitialState[Input]
  override type OutputState = InitialState[Input]


}

class DeleteTable[T <: AnyTable with Singleton]  ( table: T,  val state: InitialState[T]) extends DeleteTableAux {
  override val input: Input = table
  override type Input = T
}

object DeleteTable {

  implicit class DeleteTableExecute[D <: DeleteTableAux](ac: D) extends Execute {

    override type Action = D
    override val action = ac


    override def apply(): (action.Input, action.OutputState) = {
      println("executing: " + action)
      (action.input, action.state)
    }

    override type C[+X] = X

  }
}




class IrishDynamoDBService(credentialProvider: AWSCredentialsProvider) extends AnyDynamoDBService {

  override type Account = ohnosequences.tabula.Account
  override type Region = EU.type

  override def endpoint: String = "" //shouldn't be here

  override val account: Account = Account("", "")

  override val region = EU


  val ddbClient = new AmazonDynamoDBClient(credentialProvider)
  ddbClient.setRegion(Region.getRegion(Regions.EU_WEST_1))

//  implicit class DeleteTableExecute[T <: AnyTable with Singleton](table: T) extends Execute[ DeleteTable[T]] {
//
//
//    override def apply(action: Action): (T, InitialState[T]) = {
//      println("executing: " + action)
//      (table, action.state)
//    }
//
//    override type C[+X] = X
//
//   // override val action = new DeleteTable(table, InitialState[T](table,account,InitialThroughput(0, 0)))
//  }
}


object CredentialProviderChains {
  val default = new AWSCredentialsProviderChain(
    new InstanceProfileCredentialsProvider(),
    new EnvironmentVariableCredentialsProvider(),
    new PropertiesFileCredentialsProvider(new File(System.getProperty("user.home"), "credentials").getPath)
  )
}