package ohnosequences.tabula

// regions
sealed trait AnyRegion {
  val name: String
}

case object EU extends AnyRegion { val name = "eu-west-1" }
case object US extends AnyRegion { val name = "us-east-1" }