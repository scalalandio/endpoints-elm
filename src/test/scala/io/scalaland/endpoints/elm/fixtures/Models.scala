package io.scalaland.endpoints.elm.fixtures

import java.time.LocalDate
import java.util.UUID

sealed trait Coproduct

case class Inst1(field1: String, field2: Float, field3: Double, field4: Boolean) extends Coproduct

case class Inst2(field1: UUID, field2: LocalDate) extends Coproduct

case class Foo(foo1: Int, foo2: Long, foo3: Inst1, foo4: Option[Inst2])
