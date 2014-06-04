package ohnosequences.tabula

import ohnosequences.scarph._
import shapeless._

object oh {
  
  // the abstract part
  trait AnyResourceType
  trait AnyResource extends AnyDenotation { type TYPE <: AnyResourceType }

  trait AnyStateType {

    type ResourceType <: AnyResourceType
    val resourceType: ResourceType
  }
  trait AnyState extends AnyDenotation { type TYPE <: AnyStateType }

  trait AnyActionType {

    type InputType <: AnyResourceType
    type InputStateType <: AnyStateType { type ResourceType = InputType }

    type OutputType <: AnyResourceType
    type OutputStateType <: AnyStateType { type ResourceType = OutputType }
  }


  // tables
  trait AnyTableType extends AnyResourceType
  trait AnyTable extends AnyResource with Denotation[AnyTableType]

  trait AnyTableStateType extends AnyStateType {

    type ResourceType <: AnyTableType
  }
  trait Creating extends AnyTableStateType {}
  trait AlreadyExists extends AnyTableStateType


  trait AnyTableState extends AnyState with Denotation[AnyTableStateType]

  trait CreateTable extends AnyActionType {

    type InputType = AnyTableType
    type InputStateType = AnyTableStateType { type ResourceType = InputType }

    type OutputType = InputType
    // type OutputStateType = Creating { type ResourceType = OutputType }     :+:
                              // AlreadyExists { type ResourceType = OutputType} :+:
  }
}
