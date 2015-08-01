
```scala
package ohnosequences.tabula.impl

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScalarAttributeType, AttributeDefinition, ConditionalOperator}
import com.amazonaws.services.dynamodbv2.model.{Condition => SDKCondition}
import scala.reflect._
import shapeless._, poly._

object ImplicitConversions {
```

Conversions between item and the SDK representation

```scala
  type SDKRep = Map[String, AttributeValue]
  type SDKElem = (String, AttributeValue)

  implicit val ListLikeSDKRep = new ListLike[SDKRep] {
    type E = SDKElem

    val nil: SDKRep = Map()
    def cons(h: E, t: SDKRep): SDKRep = Map(h) ++ t

    def head(m: SDKRep): E = m.head
    def tail(m: SDKRep): SDKRep = m.tail
  }

  case object toSDKRep extends Poly1 {

    implicit def defaultString[A <: Singleton with AnyProperty, R <: RawOf[A] with String] = at[(A, R)] {

        x => ( x._1.label, new AttributeValue().withS(x._2) ): (String, AttributeValue) 
      }

    implicit def defaultNum[A <: Singleton with AnyProperty, R <: Num with RawOf[A]] = at[(A, R)] {
        
      x => {

        val key = x._1.label
        val numV: Num = x._2
        val attrV = new AttributeValue().withN(numV.toString)

        (key, attrV): (String, AttributeValue)
      }  
    }

    implicit def defaultBytes[A <: Singleton with AnyProperty, R <: Bytes with RawOf[A]] = at[(A,R)] { 
      x => {

        val key = x._1.label
        val bytes: Bytes = x._2

        import java.nio._
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(bytes.length)
        byteBuffer.put(bytes)
        val attrV = new AttributeValue().withB(byteBuffer)

        (key, attrV): (String, AttributeValue)        
      }
    }

    implicit def defaultGeneric[A <: Singleton with AnyProperty, R <: RawOf[A]] = at[(A,R)] { 

      x => {

        (x._1.label, getAttrVal[R](x._2))
      }
    }
  }

  case object fromSDKRep extends Poly1 {
    implicit def caseN[A <: Singleton with AnyProperty.ofValue[Num]] = 
      at[(SDKRep, A)]{ case (m, a) => ((a:A) ->> m(a.label).getN.toInt): TaggedWith[A] }
    implicit def caseS[A <: Singleton with AnyProperty.ofValue[String]] = 
      at[(SDKRep, A)]{ case (m, a) => ((a:A) ->> m(a.label).getS.toString): TaggedWith[A] }
    // TODO: a case for Bytes
  }

  trait SDKRepParser extends AnyTableItemAction {
    val parseSDKRep: SDKRep => item.Rep
  }

  trait SDKRepGetter extends AnyTableItemAction {
    val getSDKRep: Input => SDKRep
  }
```

Properties-related conversions

```scala
  implicit def getAttrDef[A <: Singleton with AnyProperty](attr: A): AttributeDefinition = {

    val attrDef = new AttributeDefinition().withAttributeName(attr.label)

    attr.classTag.runtimeClass.asInstanceOf[Class[A#Raw]] match {
      case c if c == classOf[Num]    => attrDef.withAttributeType(ScalarAttributeType.N)
      case c if c == classOf[String] => attrDef.withAttributeType(ScalarAttributeType.S)
      case c if c == classOf[Bytes]  => attrDef.withAttributeType(ScalarAttributeType.B)
    }
  }

  // FIXME: restrict T somehow, maybe Typeable instance is needed
  implicit def getAttrVal[T] // : Typeable] // : oneOf[NotSetValues]#is]
    (attr: T): AttributeValue = {

    // val B = TypeCase[Bytes]
    attr match {
      case _: Num    => new AttributeValue().withN(attr.toString)
      case _: String => new AttributeValue().withS(attr.toString)
      // TODO: test the Bytes case
      case a: Bytes => { 
        import java.nio._
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(a.length)
        byteBuffer.put(Array[Byte](a: _*))
        new AttributeValue().withB(byteBuffer)
      }
    }
  }
```

Conditions-related conversions

```scala
  implicit def toSDKCondition[C <: Condition](cond: C): SDKCondition = {
    import scala.collection.JavaConversions._

    val sdkCond = new SDKCondition().withComparisonOperator(cond.getClass.getSimpleName)

    cond match {
      case c: NullaryCondition[_] => sdkCond
      case _ => {
        val attrValList: java.util.Collection[AttributeValue] = (cond match {
          case c:  SimpleCondition[_] => List(c.value)
          case c:      CONTAINS[_, _] => List(c.value)
          case c:  NOT_CONTAINS[_, _] => List(c.value)
          case c:          BETWEEN[_] => List(c.start, c.end)
          case c:               IN[_] => c.values
        }) map getAttrVal

        sdkCond.withAttributeValueList(attrValList)
      }
    }
  }

  implicit def toSDKPredicate[P <: AnyPredicate](pred: P): (ConditionalOperator, Map[String, SDKCondition]) = {

    pred match {
      case p: AnySimplePredicate => (ConditionalOperator.AND, Map(p.head.property.label -> toSDKCondition(p.head)))
      case p: AnyAndPredicate => (ConditionalOperator.AND, 
                                  toSDKPredicate(p.body)._2 + (p.head.property.label -> toSDKCondition(p.head)))
      case p:  AnyOrPredicate => (ConditionalOperator.OR,
                                  toSDKPredicate(p.body)._2 + (p.head.property.label -> toSDKCondition(p.head)))
    }
  }
    
}

```


------

### Index

+ src
  + main
    + scala
      + tabula
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
          + [Query.scala][main/scala/tabula/actions/Query.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + impl
          + actions
            + [GetItem.scala][main/scala/tabula/impl/actions/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/actions/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/actions/Query.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/executors/Query.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
          + [ImplicitConversions.scala][main/scala/tabula/impl/ImplicitConversions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
      + [tabula.scala][main/scala/tabula.scala]
  + test
    + scala
      + tabula
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
        + [items.scala][test/scala/tabula/items.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]

[main/scala/tabula/accounts.scala]: ../accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: ../actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: ../actions.scala.md
[main/scala/tabula/conditions.scala]: ../conditions.scala.md
[main/scala/tabula/executors.scala]: ../executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: ../items.scala.md
[main/scala/tabula/predicates.scala]: ../predicates.scala.md
[main/scala/tabula/regions.scala]: ../regions.scala.md
[main/scala/tabula/resources.scala]: ../resources.scala.md
[main/scala/tabula/services.scala]: ../services.scala.md
[main/scala/tabula/states.scala]: ../states.scala.md
[main/scala/tabula/tables.scala]: ../tables.scala.md
[main/scala/tabula.scala]: ../../tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../../test/scala/tabula/impl/irishService.scala.md
[test/scala/tabula/items.scala]: ../../../../test/scala/tabula/items.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../../../../test/scala/tabula/simpleModel.scala.md