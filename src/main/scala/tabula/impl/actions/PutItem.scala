// package ohnosequences.tabula.impl.actions

// import ohnosequences.typesets._
// import ohnosequences.scarph._
// import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// import ohnosequences.tabula._, impl._, ImplicitConversions._

// case class InHashKeyTable[T <: Singleton with AnyTable.withHashKey]
//   (state: AnyTableState.For[T] with ReadyTable) {

//   case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

//     case class withValue(itemRep: i.Rep)(implicit
//       val transf: FromProperties.Item[i.type, SDKRep],
//       val hasHashKey:  state.table.HashKey  ∈ i.Properties
//     ) extends AnyPutItemAction with SDKRepGetter {
//       type Table = T
//       val  table = state.table

//       type Item = I
//       val  item = i: i.type

//       val  input = itemRep
//       val  getSDKRep = (r: i.Rep) => transf(r)

//       val inputState = state

//       override def toString = s"InTable ${state.table.name} putItem ${i.label} withValue ${itemRep}"
//     }

//   }

// }


// case class InCompositeKeyTable[T <: Singleton with AnyTable.withCompositeKey]
//   (state: AnyTableState.For[T] with ReadyTable) {

//   case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

//     case class withValue(itemRep: i.Rep)(implicit
//       val transf: FromProperties.Item[i.type, SDKRep],
//       val hasHashKey:  state.table.HashKey  ∈ i.Properties,
//       val hasRangeKey: state.table.RangeKey ∈ i.Properties 
//     ) extends AnyPutItemAction with SDKRepGetter {
//       type Table = T
//       val  table = state.table

//       type Item = I
//       val  item = i: i.type

//       val  input = itemRep
//       val  getSDKRep = (r: i.Rep) => transf(r)

//       val inputState = state

//       override def toString = s"InTable ${state.resource.name} putItem ${i.label} withValue ${itemRep}"
//     }

//   }

// }
