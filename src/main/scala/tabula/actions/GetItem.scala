// package ohnosequences.tabula

// import ohnosequences.pointless._, AnyType._
// import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// import ohnosequences.tabula.impl.ImplicitConversions._

// trait AnyGetItemAction extends AnyItemAction {
//   //require updating or creating
//   type InputState  = AnyTableState.For[TableOf[Item]] with ReadyTable
//   type OutputState = InputState

//   type Input = PrimaryKeyValue[TableOf[Item]#PrimaryKey]
//   type Output = ValueOf[Item]
// }
