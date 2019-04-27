package io.scalaland.endpoints.elm

import endpoints.algebra
import io.scalaland.endpoints.elm.model._

import scala.language.higherKinds
import scala.collection.compat.Factory

trait JsonSchemas extends algebra.JsonSchemas {
  type JsonSchema[A] = ElmType
  type Record[A] = TypeAlias
  type Tagged[A] = UnionType
  type Enum[A] = UnionType

  def enumeration[A](values: Seq[A])(encode: A => String)(implicit tpe: JsonSchema[String]): UnionType =
    UnionType("", values.map(v => encode(v) -> TypeAlias("", Nil)))

  def named[A, S[T] <: JsonSchema[T]](schema: S[A], name: String): S[A] = schema match {
    case ta: TypeAlias => ta.copy(name = name).asInstanceOf[S[A]]
    case ut: UnionType => ut.copy(name = name).asInstanceOf[S[A]]
    case other         => other
  }

  def lazySchema[A](schema: => JsonSchema[A], name: String): JsonSchema[A] =
    schema

  def emptyRecord: TypeAlias = TypeAlias("", Seq.empty)

  def field[A](name: String, documentation: Option[String])(implicit tpe: ElmType): TypeAlias =
    TypeAlias("", Seq(name -> tpe))

  def optField[A](name: String, documentation: Option[String])(implicit tpe: ElmType): TypeAlias =
    TypeAlias("", Seq(name -> AppliedType.Maybe(tpe)))

  def taggedRecord[A](recordA: TypeAlias, tag: String): UnionType =
    UnionType("", Seq(tag -> recordA))

  def withDiscriminator[A](tagged: UnionType, discriminatorName: String): UnionType =
    tagged.copy(discriminator = Some(discriminatorName))

  def choiceTagged[A, B](taggedA: UnionType, taggedB: UnionType): UnionType =
    taggedA.copy(constructors = taggedA.constructors ++ taggedB.constructors)

  def zipRecords[A, B](recordA: TypeAlias, recordB: TypeAlias): TypeAlias =
    recordA.copy(fields = recordA.fields ++ recordB.fields)

  def xmapRecord[A, B](record: TypeAlias, f: A => B, g: B => A): TypeAlias = record

  def xmapTagged[A, B](taggedA: UnionType, f: A => B, g: B => A): UnionType = taggedA

  def xmapJsonSchema[A, B](jsonSchema: ElmType, f: A => B, g: B => A): ElmType = jsonSchema

  implicit def uuidJsonSchema: ElmType = BasicType.Uuid

  implicit def stringJsonSchema: ElmType = BasicType.String

  implicit def intJsonSchema: ElmType = BasicType.Int

  implicit def longJsonSchema: ElmType = BasicType.Int

  implicit def bigdecimalJsonSchema: ElmType = BasicType.String

  implicit def floatJsonSchema: ElmType = BasicType.Float

  implicit def doubleJsonSchema: ElmType = BasicType.Float

  implicit def booleanJsonSchema: ElmType = BasicType.Bool

  implicit def arrayJsonSchema[C[X] <: scala.Seq[X], A](implicit jsonSchema: JsonSchema[A],
                                                        factory: Factory[A, C[A]]): JsonSchema[C[A]] =
    AppliedType.List(jsonSchema)

  implicit def mapJsonSchema[A](implicit tpe: JsonSchema[A]): JsonSchema[Map[String, A]] =
    AppliedType.Dict(tpe)
}
