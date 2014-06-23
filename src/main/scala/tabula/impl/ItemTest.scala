package ohnosequences.tabula.impl.itemtest

import ohnosequences.tabula.impl.itemtest.Builder.Of


trait AnyAttribute {
  val label: String
  type Raw
}

trait AnyIntAttribute extends AnyAttribute {
  override type Raw = Int
}

trait AnyStringAttribute extends AnyAttribute {
  override type Raw = String
}

case class IntAttribute(label: String) extends AnyIntAttribute

case class StringAttribute(label: String) extends AnyStringAttribute



trait Builder {
  type Item <: AnyItem
  val item: Item

  def result(): item.Rep

  def addAttribute[A <: AnyAttribute](attribute: A)(value: attribute.Raw)
}

object Builder {
  type Of[I <: AnyItem] = Builder { type Item = I }
}


trait AnyItem {
  type Rep
  def get[A <: AnyAttribute](a: A, rep: Rep): a.Raw

  def builder(): Builder.Of[this.type]

 // def map[R](f: AnyAttribute => R)
}


object id extends IntAttribute("id")

object name extends StringAttribute("name")

object TestItem extends AnyItem {
  //todo add has attribute


  override type Rep = (Int, String)

  override def get[A <: AnyAttribute ](a: A, rep: Rep): a.Raw = {
    a match {
      case IntAttribute(label) => rep._1.asInstanceOf[a.Raw]
      case StringAttribute(label) => rep._2.asInstanceOf[a.Raw]
    }
  }

  override def builder(): Of[TestItem.type] = new Builder {

    override type Item = TestItem.type

    override val item: Item = TestItem

    var idValue: Int = 0
    var nameValue: String = ""

    override def addAttribute[A <: AnyAttribute](attribute: A)(value: attribute.Raw) {
      if (attribute.equals(id)) {
        idValue = value.asInstanceOf[Int]
      } else if (attribute.equals(name)) {
        nameValue =  value.asInstanceOf[String]
      }
    }

    override def result() = (idValue, nameValue)

  }
}


