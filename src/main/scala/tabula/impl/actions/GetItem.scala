// // package ohnosequences.tabula.impl.actions

// <<<<<<< HEAD
// import ohnosequences.cosas._, types._, typeSets._, fns._
// import ohnosequences.cosas.ops.record._
// import ohnosequences.cosas.ops.typeSets._

// import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// import ohnosequences.tabula._, impl._, ImplicitConversions._

// case class FromHashKeyTable[T <: AnyHashKeyTable]
//   (val table: T, val inputSt: AnyTableState.For[T] with ReadyTable) { fromHashKeyTable =>

//   case class getItem[I <: AnyItem with AnyItem.ofTable[T]](val item: I) { getItem =>

//     case class withKey (
//       hashKeyValue: RawOf[T#HashKey]
//     )
//     (implicit
//       val parser: (I#Properties ParseFrom SDKRep) with out[RawOf[I]],
//       val hasHashKey: T#HashKey ∈ I#Properties
//     ) 
//     extends AnyGetItemHashKeyAction with SDKRepParser {

//       type Table = T
//       val table = fromHashKeyTable.table

//       type Item = I
//       val item = getItem.item
// =======
// // import ohnosequences.typesets._
// // import ohnosequences.scarph._
// // import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// // import ohnosequences.tabula._, impl._, ImplicitConversions._

// // case class FromHashKeyTable[T <: Singleton with AnyTable.withHashKey]
// //   (state: AnyTableState.For[T] with ReadyTable) {

// //   case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

// //     case class withKey(hashKeyValue: state.resource.hashKey.Raw)
// //     (implicit
// //       val form: ToItem[SDKRep, i.type],
// //       val hasHashKey: state.resource.HashKey ∈ i.Properties
// //     ) extends AnyGetItemHashKeyAction with SDKRepParser {
// //       type Table = T
// //       val  table = state.resource: state.resource.type

// //       type Item = I
// //       val  item = i: i.type
// >>>>>>> feature/table/ops

// //       val input = hashKeyValue

// //       val inputState = state

// <<<<<<< HEAD
//       val parseSDKRep = (m: SDKRep) => { item =>> parser(item.properties, m) }

//       override def toString = s"FromTable ${table.name} getItem ${item.toString} withKey ${hashKeyValue}"
//     }
// =======
// //       val parseSDKRep = (m: SDKRep) => form(m, i)

// //       override def toString = s"FromTable ${state.resource.name} getItem ${i.label} withKey ${hashKeyValue}"
// //     }
// >>>>>>> feature/table/ops

// //   }

// // }


// <<<<<<< HEAD
// /* ### Composite key table */
// case class FromCompositeKeyTable[T <: AnyCompositeKeyTable]
//   (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

//   case class getItem[I <: AnyItem.ofTable[T]](i: I) {

//     case class withKeys(
//       hashKeyValue: RawOf[T#HashKey],
//       rangeKeyValue: RawOf[T#RangeKey]
//     )(implicit
//       val parser: (I#Properties ParseFrom SDKRep) with out[RawOf[I]],
//       val hasHashKey:  T#HashKey  ∈ I#Properties,
//       val hasRangeKey: T#RangeKey ∈ I#Properties
//     ) 
//     extends AnyGetItemCompositeKeyAction with SDKRepParser {
      
//       type Table = T
//       val  table = t

//       type Item = I
//       val  item = i
// =======
// // /* ### Composite key table */
// // case class FromCompositeKeyTable[T <: Singleton with AnyTable.withCompositeKey]
// //   (state: AnyTableState.For[T] with ReadyTable) {

// //   case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

// //     case class withKeys(
// //       hashKeyValue: state.resource.hashKey.Raw,
// //       rangeKeyValue: state.resource.rangeKey.Raw
// //     )(implicit
// //       val form: ToItem[SDKRep, i.type],
// //       val hasHashKey:  state.resource.HashKey  ∈ i.Properties,
// //       val hasRangeKey: state.resource.RangeKey ∈ i.Properties
// //     ) extends AnyGetItemCompositeKeyAction with SDKRepParser {
// //       type Table = T
// //       val  table = state.resource: state.resource.type

// //       type Item = I
// //       val  item = i: i.type
// >>>>>>> feature/table/ops

// //       val input = (hashKeyValue, rangeKeyValue)

// //       val inputState = state

// <<<<<<< HEAD
//       val parseSDKRep = (m: SDKRep) => { item =>> parser(item.properties, m) }

//       override def toString = s"FromTable ${t.name} getItem ${i.toString} withKeys ${(hashKeyValue, rangeKeyValue)}"
//     }
// =======
// //       val parseSDKRep = (m: SDKRep) => form(m, i)

// //       override def toString = s"FromTable ${state.resource.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
// //     }
// >>>>>>> feature/table/ops

// //   }

// // }
