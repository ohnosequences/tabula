package ohnosequences.tabula.test

import ohnosequences.tabula._

object ResourceLists {
  
  import simpleModel._

  type TT = AnyTable :+: AnyTable :+: RNil 
  val uh: TT = UsersTable :+: RandomTable :+: RNil

  trait AnyAction {

    type Resources <: ResourceList
  }
  trait AnyJoinTables extends AnyAction {

    type Resources <: AnyTable :+: AnyTable :+: RNil
    val resources: Resources

    val scndTblKey = resources.tail.head.hashKey
  }

  case class JoinTables[
    T0 <: Singleton with AnyTable,
    T1 <: Singleton with AnyTable
  ]
  (
    val t0: T0,
    val t1: T1
  ) extends AnyJoinTables {

    type Resources = T0 :+: T1 :+: RNil
    val resources = t0 :+: t1 :+: RNil
  }
}