// package ohnosequences.tabula.action

// import com.amazonaws.services.dynamodbv2.model.AttributeValue
// import ohnosequences.cosas.types._

// sealed trait DeleteItemResult
// case object DeleteItemFail extends DeleteItemResult
// case object DeleteItemSuccess extends DeleteItemResult

// /* ### Common action trait */
// trait AnyDeleteItemAction extends AnyTableAction {
//   //require updating or creating
//   type InputState  = AnyTableState.For[Table] with ReadyTable
//   type OutputState = InputState

//   type Output = DeleteItemResult
// }


// /* ### Hash key table */
// <<<<<<< HEAD
// trait AnyDeleteItemHashKeyAction extends AnyDeleteItemAction {

//   type Table <: AnyHashKeyTable
//   type Input = RawOf[Table#HashKey]
// }

// case class DeleteItemFromHashKeyTable[T <: AnyHashKeyTable]
// (
//   val t: T,
//   val inputSt: AnyTableState.For[T] with ReadyTable
// )
// {

//   case class withKey(hashKeyValue: RawOf[T#HashKey]) extends AnyDeleteItemHashKeyAction {

//     type Table = T
//     val  table = t
// =======
// // trait AnyDeleteItemHashKeyAction extends AnyDeleteItemAction {
// //   type Table <: Singleton with AnyTable.withHashKey
// //   type Input = table.hashKey.Raw
// // }

// // case class DeleteItemFromHashKeyTable[T <: Singleton with AnyTable.withHashKey]
// //   (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

// //   case class withKey(hashKeyValue: t.hashKey.Raw)
// //    extends AnyDeleteItemHashKeyAction {

// //     type Table = T
// //     val  table = t: t.type
// >>>>>>> feature/table/ops

// //     val input = hashKeyValue

// //     val inputState = inputSt

// //     override def toString = s"DeleteItemFromHashKeyTable ${t.name} withKey ${hashKeyValue}"
// //   }

// // }


// <<<<<<< HEAD
// /* ### Composite key table */
// trait AnyDeleteItemCompositeKeyAction extends AnyDeleteItemAction {

//   type Table <: AnyCompositeKeyTable
//   type Input = ( RawOf[Table#HashKey], RawOf[Table#RangeKey] )
// }

// case class DeleteItemFromCompositeKeyTable[T <: AnyCompositeKeyTable]
//   (val t: T, val inputSt: AnyTableState.For[T] with ReadyTable) {

//   case class withKeys(
//     val hashKeyValue: RawOf[T#HashKey],
//     val rangeKeyValue: RawOf[T#RangeKey]
//   )
//   extends AnyDeleteItemCompositeKeyAction {

//     type Table = T
//     val  table = t
// =======
// // /* ### Composite key table */
// // trait AnyDeleteItemCompositeKeyAction extends AnyDeleteItemAction {
// //   type Table <: Singleton with AnyTable.withCompositeKey
// //   type Input = (table.hashKey.Raw, table.rangeKey.Raw)
// // }

// // case class DeleteItemFromCompositeKeyTable[T <: Singleton with AnyTable.withCompositeKey]
// //   (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

// //   case class withKeys(
// //     hashKeyValue: t.hashKey.Raw,
// //     rangeKeyValue: t.rangeKey.Raw
// //   ) extends AnyDeleteItemCompositeKeyAction {
// //     type Table = T
// //     val  table = t: t.type
// >>>>>>> feature/table/ops

// //     val input = (hashKeyValue, rangeKeyValue)

// //     val inputState = inputSt

// //     override def toString = s"DeleteItemFromCompositeKeyTable ${t.name} withKeys ${(hashKeyValue, rangeKeyValue)}"
// //   }

// // }
