package ohnosequences.tabula.impl.actions

import ohnosequences.tabula._, impl._, ImplicitConversions._

case class PutItem[I <: Singleton with AnyItem](val i: I) {
  case class withValue(val itemRep: i.Rep)(implicit
    val transf: FromProperties.Item[i.type, SDKRep]
  ) extends AnyPutItemAction with SDKRepGetter {

    type Item = I
    val  item = i: i.type

    val  input = itemRep

    val  getSDKRep = (r: Input) => transf(r)
  }
}
