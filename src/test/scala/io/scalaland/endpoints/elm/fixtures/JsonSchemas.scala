package io.scalaland.endpoints.elm.fixtures

import java.time.LocalDate

import io.scalaland.endpoints.macros

import scala.reflect.ClassTag

trait JsonSchemas extends macros.JsonSchemas {

  override def typeName(ct: ClassTag[_]): String =
    TypeNames.noPackageName(ct)

  implicit def dateSchema: JsonSchema[LocalDate]

  implicit def coproductSchema: JsonSchema[Coproduct] =
    genericJsonSchema[Coproduct]

  implicit def inst1Schema: JsonSchema[Inst1] =
    genericJsonSchema[Inst1]

  implicit def inst2Schema: JsonSchema[Inst2] =
    genericJsonSchema[Inst2]

  implicit def fooSchema: JsonSchema[Foo] =
    genericJsonSchema[Foo]

}
