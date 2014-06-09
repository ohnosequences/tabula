
```scala
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




```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [queries.scala][main/scala/tabula/queries.scala]

[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula/attributes.scala]: attributes.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/queries.scala]: queries.scala.md