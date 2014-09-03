// package ohnosequences.tabula.impl.actions

// <<<<<<< HEAD
// import ohnosequences.pointless._, AnyTypeSet._, AnyRecord._, AnyTaggedType.Tagged
// import ohnosequences.pointless.ops.record._
// import ohnosequences.pointless.ops.typeSet._

// import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
// import ohnosequences.tabula._, impl._, ImplicitConversions._, AnyItem._

// case class InHashKeyTable[T <: AnyHashKeyTable]
//   (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

//   case class putItem[I <: AnyItem.ofTable[T]](val i: I) {

//     case class withValue(val itemRaw: RawOf[I])(implicit
//       val serializer: RawOf[I] SerializeTo SDKRep,
//       val hasHashKey:  T#HashKey ∈ I#Properties
//     ) 
//     extends AnyPutItemAction with SDKRepGetter {

//       type Table = T
//       val  table = t

//       type Item = I
//       val  item = i

//       val  input = item =>> itemRaw

//       val  getSDKRep = (r: RawOf[Item]) => serializer(r: RawOf[Item])

//       val inputState = inputSt

//       override def toString = s"InTable ${t.name} putItem ${i.toString} withValue ${itemRaw}"
//     }
// =======
// import ohnosequences.tabula._, impl._, ImplicitConversions._

// case class PutItem[I <: Singleton with AnyItem](val i: I) {
//   case class withValue(val itemRep: i.Rep)(implicit
//     val transf: FromProperties.Item[i.type, SDKRep]
//   ) extends AnyPutItemAction with SDKRepGetter {

//     type Item = I
//     val  item = i: i.type

//     val  input = itemRep
// >>>>>>> feature/table/ops

//     val  getSDKRep = (r: Input) => transf(r)
//   }
// <<<<<<< HEAD

// }


// case class InCompositeKeyTable[T <: AnyCompositeKeyTable]
//   (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

//   case class putItem[I <:AnyItem.ofTable[T]](i: I) {

//     case class withValue(itemRaw: RawOf[I])(implicit
//       val serializer: RawOf[I] SerializeTo SDKRep,
//       val hasHashKey:  T#HashKey  ∈ I#Properties,
//       val hasRangeKey: T#RangeKey ∈ I#Properties 
//     ) 
//     extends AnyPutItemAction with SDKRepGetter {
//       type Table = T
//       val  table = t

//       type Item = I
//       val  item = i

//       val  input = item =>> itemRaw
//       val  getSDKRep = (r: RawOf[Item]) => serializer(r: RawOf[Item])
      
//       val inputState = inputSt

//       override def toString = s"InTable ${t.name} putItem ${i.toString} withValue ${itemRaw}"
//     }

//   }

// =======
// >>>>>>> feature/table/ops
// }

// case class putputput[I <: AnyItem](
//   inputSt: AnyTableState.For[TableOf[I]] with ReadyTable,
//   itemRaw: Tagged[I]
// )(implicit
//   val getI: Tagged[I] => I,
//   val serializer: RawOf[I] SerializeTo SDKRep,
//   val hasHashKey:  TableOf[I]#HashKey  ∈ PropertiesOf[I] //,
//   // val hasRangeKey: TableOf[I]#RangeKey ∈ PropertiesOf[I] 
// ) extends AnyPutItemAction with SDKRepGetter {

//   type Item = I
//   val  item = getI(itemRaw)

//   type Table = TableOf[I]
//   val  table = item.table

//   val  input = item =>> itemRaw

//   val  getSDKRep = (r: RawOf[Item]) => serializer(r: RawOf[Item])
  
//   val inputState = inputSt

//   override def toString = s"InTable ${table.name} putItem ${item.toString} withValue ${itemRaw}"
// }
