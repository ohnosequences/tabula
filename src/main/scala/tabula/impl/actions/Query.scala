// // package ohnosequences.tabula.impl.actions

// <<<<<<< HEAD
// import ohnosequences.pointless._, AnyTypeSet._, AnyRecord._, AnyFn._
// import ohnosequences.pointless.ops.record._
// import ohnosequences.pointless.ops.typeSet._

// import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// import ohnosequences.tabula._, impl._, ImplicitConversions._

// case class QueryTable[T <: AnyCompositeKeyTable]
//   (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

//   case class forItem[I <: AnyItem with AnyItem.ofTable[T]](val i: I) {

//     case class withHashKey(hashKeyValue: T#HashKey#Raw)
//     (implicit 
//       val parser: (I#Properties ParseFrom SDKRep) with out[RawOf[I]],
//       val hasHashKey: T#HashKey ∈ I#Properties
//     ) 
//     extends AnySimpleQueryAction with SDKRepParser { self =>

//       type Table = T
//       val  table = t

//       type Item = I
//       val  item = i
// =======
// // import ohnosequences.typesets._
// // import ohnosequences.scarph._
// // import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// // import ohnosequences.tabula._, impl._, ImplicitConversions._

// // case class QueryTable[T <: Singleton with AnyTable.withCompositeKey]
// //   (state: AnyTableState.For[T] with ReadyTable) {

// //   case class forItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

// //     case class withHashKey(hashKeyValue: state.resource.hashKey.Raw)(implicit 
// //       val parser: ToItem[SDKRep, i.type], 
// //       val hasHashKey: state.resource.HashKey ∈ i.Properties
// //     ) extends AnySimpleQueryAction
// //          with SDKRepParser { self =>

// //       type Table = T
// //       val  table = state.resource: state.resource.type

// //       type Item = I
// //       val  item = i: i.type
// >>>>>>> feature/table/ops

// //       val input = SimplePredicate(item, EQ(table.hashKey, hashKeyValue))

// <<<<<<< HEAD
//       val inputState = inputSt
//       val parseSDKRep = (m: SDKRep) => { item =>> parser(item.properties, m) }

//       override def toString = s"QueryTable ${t.name} forItem ${i.toString} withHashKey ${hashKeyValue}"

//       case class andRangeCondition[C <: Condition.On[T#RangeKey] with KeyCondition](c: C)
//           extends AnyNormalQueryAction
//              with SDKRepParser {

//           type Table = T
//           val  table = t

//           type Item = I
//           val  item = i

//           type RangeCondition = C
//           val  rangeCondition = c
// =======
// //       val inputState = state
// //       val parseSDKRep = (m: SDKRep) => parser(m, i)

// //       override def toString = s"QueryTable ${state.resource.name} forItem ${i.label} withHashKey ${hashKeyValue}"

// //       case class andRangeCondition[C <: Condition.On[state.resource.RangeKey] with KeyCondition](c: C)
// //           extends AnyNormalQueryAction
// //              with SDKRepParser {

// //           type Table = T
// //           val  table = state.resource: state.resource.type

// //           type Item = I
// //           val  item = i: i.type

// //           type RangeCondition = C
// //           val  rangeCondition = c: c.type
// >>>>>>> feature/table/ops

// //           val input = AND(SimplePredicate(item, EQ(table.hashKey, hashKeyValue)), c)

// <<<<<<< HEAD
//           val inputState = inputSt
//           val parseSDKRep = (m: SDKRep) => { item =>> parser(item.properties, m) }
//           val hasHashKey = self.hasHashKey

//           override def toString = s"QueryTable ${t.name} forItem ${i.toString} withHashKey ${hashKeyValue} andRangeCondition ${rangeCondition}"
//       }
//     }
//   }
// }
// =======
// //           val inputState = state
// //           val parseSDKRep = (m: SDKRep) => parser(m, i)
// //           val hasHashKey = self.hasHashKey

// //           override def toString = s"QueryTable ${state.resource.name} forItem ${i.label} withHashKey ${hashKeyValue} andRangeCondition ${rangeCondition}"
// //       }
// //     }
// //   }
// // }
// >>>>>>> feature/table/ops
