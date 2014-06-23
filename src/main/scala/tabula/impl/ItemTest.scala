package ohnosequences.tabula.impl.itemtest


trait AnyAttribute {
  val label: String
  type Raw
}

trait AnyIntAttribute extends AnyAttribute {
  override type Raw = Int
}

case class IntAttribute(label: String) extends AnyIntAttribute

case class StringAttribute(label: String) extends AnyIntAttribute


trait AnyStringAttribute extends AnyAttribute {
  override type Raw = String
}



trait Item {
  type Rep
  def get[A <: AnyAttribute](a: A, rep: Rep): a.Raw

 // def map[R](f: AnyAttribute => R)
}


object id extends IntAttribute("id")

object name extends IntAttribute("name")

object TestItem extends Item {
  //todo add has attribute


  override type Rep = (Int, String)

  override def get[A <: AnyAttribute ](a: A, rep: Rep): a.Raw = {
    a match {
      case IntAttribute(label) => rep._1.asInstanceOf[a.Raw]
      case StringAttribute(label) => rep._2.asInstanceOf[a.Raw]
    }
  }

}
