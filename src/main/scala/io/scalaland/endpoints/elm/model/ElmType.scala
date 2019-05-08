package io.scalaland.endpoints.elm.model

sealed trait ElmType { def name: String }

sealed abstract class BasicType(val name: String) extends ElmType

object CustomBasicType {
  def apply(name: String): CustomBasicType =
    CustomBasicType(
      name,
      s"$name.init",
      s"$name.encoder",
      s"$name.decoder",
      s"$name.toString"
    )
}

case class CustomBasicType(override val name: String,
                           initExpr: String,
                           encoderExpr: String,
                           decoderExpr: String,
                           toStringExpr: String)
  extends BasicType(name)

object BasicType {
  object Unit extends BasicType("()")
  object String extends BasicType("String")
  object Int extends BasicType("Int")
  object Float extends BasicType("Float")
  object Bool extends BasicType("Bool")
  object Uuid extends BasicType("Uuid")
}

sealed abstract class AppliedType(val name: String, val args: Seq[ElmType]) extends ElmType

object AppliedType {
  case class Maybe(tpe: ElmType) extends AppliedType("Maybe", Seq(tpe))
  case class List(tpe: ElmType) extends AppliedType("List", Seq(tpe))
  case class Dict(tpe: ElmType) extends AppliedType("Dict", Seq(BasicType.String, tpe))
}

case class ReferencedType(name: String) extends ElmType

case class TypeAlias(name: String, fields: Seq[(String, ElmType)]) extends ElmType

case class UnionType(name: String, constructors: Seq[(String, TypeAlias)], discriminator: Option[String] = None)
    extends ElmType

object ElmType {

  def referencesDeep(elmType: ElmType): Seq[ElmType] = elmType match {
    case _: BasicType =>
      Nil
    case appliedType: AppliedType =>
      appliedType.args.flatMap(referencesDeep)
    case _: ReferencedType =>
      Nil
    case ta @ TypeAlias(_, fields) =>
      ta +: fields.flatMap { case (_, tpe) => referencesDeep(tpe) }
    case ut @ UnionType(_, constructors, _) =>
      ut +: constructors.flatMap { case (_, tpe) => referencesDeep(tpe) }
  }

  def referencesShallow(elmType: ElmType): Seq[ElmType] = elmType match {
    case cbt: CustomBasicType =>
      Seq(cbt)
    case BasicType.Uuid =>
      Seq(BasicType.Uuid)
    case _: BasicType =>
      Nil
    case appliedType: AppliedType =>
      appliedType.args.flatMap(referencesShallow)
    case _: ReferencedType =>
      Nil
    case ta: TypeAlias =>
      Seq(ta)
    case ut: UnionType =>
      Seq(ut)
  }

}
