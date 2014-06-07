// package ohnosequences.tabula

// import shapeless._

// import ohnosequences.scarph._

// trait AnyDynamoDBActionType {

//   type InputType <: AnyDynamoDBResourceType
//   val inputType: InputType

//   type InputStateType <: AnyDynamoDBStateType.Of[InputType]
//   val inputStateType: InputStateType

//   type OutputType <: AnyDynamoDBResourceType
//   val outputType: OutputType

//   type OutputStateType <: AnyDynamoDBStateType.Of[OutputType]
//   val outputStateType: OutputStateType

//   // signal those outputs that are considered errors
//   type Errors <: AnyDynamoDBStateType.Of[OutputType]

//   // why not; should be applicative at least to unpack the output
//   type Out[+X]
// }

// trait AnyDynamoDBAction extends Denotation[AnyDynamoDBActionType] { action =>


//   type InputState <: AnyDynamoDBState.Of[Input]
//   // the point here is that you can match on both 
//   // - the "standard" output
//   // - the action-specific errors (if any)
//   type OutputState  <: AnyDynamoDBState.Of[Output] :+: Errors :+: CNil

//   def perform[
//     X <: Singleton with AnyDynamoDBResource
//   ](x: input.Rep, s: InputState)(implicit 
//     impl: AnyDynamoDBActionImpl
//   ): Out[(output.Rep, OutputState)] = impl(x,s)

//   trait AnyDynamoDBActionImpl {

//     type ActionType 
//     def apply(x: action.input.Rep, s: action.InputState): action.Out[(action.output.Rep, action.OutputState)]
// }

// }

// /*
//   #### examples

//   Imagine that you try to create a table. This table already has a table type, which is statically defined. 


//   Things can go wrong in a lot of different ways

// */



