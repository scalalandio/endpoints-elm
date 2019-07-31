package io.scalaland.endpoints.elm.emit

import io.scalaland.endpoints.elm.model.AppliedType.Dict
import io.scalaland.endpoints.elm.model._

object TypeEmit {

  def moduleDefinition(elmType: ElmType): String =
    List(
      Commons.headerComment,
      s"module Data.${elmType.name} exposing (..)",
      "",
      imports(elmType).distinct.sorted.map(i => s"import $i exposing (..)").mkString("\n"),
      jsonImports,
      "",
      typeDefinition(elmType),
      "",
      s"init : ${elmType.name}",
      s"init =${initDefinition(elmType)}",
      "",
      s"decoder : Decoder ${elmType.name}",
      s"decoder = ${decoderDefinition(elmType)}",
      "",
      s"encoder : ${elmType.name} -> Encode.Value",
      s"encoder model = ${encoderDefinition(elmType, "model")}",
      "",
      recordSetters(elmType).mkString("\n"),
      "",
      recordUpdaters(elmType).mkString("\n")
    ).mkString("\n")

  private val jsonImports =
    s"""import Json.Decode as Decode exposing (Decoder)
       |import Json.Decode.Pipeline exposing (optional, required)
       |import Json.Encode as Encode
       |""".stripMargin

  def typeDefinition(elmType: ElmType): String = elmType match {
    case TypeAlias(name, fields) =>
      s"type alias $name =" + fields
        .map {
          case (nme, tpe) =>
            s"$nme : ${ElmType.typeReference(tpe)}"
        }
        .mkString("\n  { ", "\n  , ", "\n  }")
    case UnionType(name, constructors, _) =>
      constructors
        .map {
          case (nme, param) =>
            s"${unionConstructorName(name, nme)} ${ElmType.typeReference(param)}"
        }
        .mkString(s"type $name\n  = ", "\n  | ", "")
    case other =>
      ElmType.typeReference(other)
  }

  def initDefinition(elmType: ElmType, topLevel: Boolean = true): String = elmType match {
    case basicType: BasicType =>
      basicType match {
        case cbt: CustomBasicType => cbt.initExpr
        case BasicType.Unit       => "()"
        case BasicType.String     => "\"\""
        case BasicType.Int        => "0"
        case BasicType.Float      => "0.0"
        case BasicType.Bool       => "False"
        case BasicType.Uuid       => "Tuple.first <| step Uuid.uuidGenerator (initialSeed 0)"
        case BasicType.Bytes      => "{- initial values for Bytes type not supported in endpoints-elm -}"
      }
    case appliedType: AppliedType =>
      appliedType match {
        case AppliedType.Maybe(_)          => "Nothing"
        case AppliedType.List(_)           => "[]"
        case AppliedType.Dict(_)           => "Dict.empty"
        case AppliedType.Result(errTpe, _) => s"Err (${initDefinition(errTpe, topLevel = false)})"
      }
    case ReferencedType(name) => s"Data.$name.init"
    case TypeAlias(_, fields) if topLevel =>
      fields
        .map {
          case (name, tpe) =>
            s"$name = ${initDefinition(tpe, topLevel = false)}"
        }
        .mkString("\n  { ", "\n  , ", "\n  }")
    case TypeAlias(name, _) =>
      initDefinition(ReferencedType(name))
    case UnionType(name, constructors, _) =>
      val (firstConstructor, arg) = constructors.head
      s"${unionConstructorName(name, firstConstructor)} ${initDefinition(arg, topLevel = false)}"
  }

  def encoderDefinition(elmType: ElmType, arg: String, topLevel: Boolean = true): String = elmType match {
    case basicType: BasicType =>
      basicType match {
        case cbt: CustomBasicType => s"(${cbt.encoderExpr}) $arg"
        case BasicType.Unit       => "Encode.object []"
        case BasicType.String     => s"Encode.string $arg"
        case BasicType.Int        => s"Encode.int $arg"
        case BasicType.Float      => s"Encode.float $arg"
        case BasicType.Bool       => s"Encode.bool $arg"
        case BasicType.Uuid       => s"Uuid.encode $arg"
        case BasicType.Bytes      => s"{- json encoding for Bytes not supported in endpoints-elm! -}"
      }
    case appliedType: AppliedType =>
      appliedType match {
        case AppliedType.Maybe(tpe) =>
          s"Maybe.withDefault Encode.null (Maybe.map ${encoderDefinition(tpe, "", topLevel = false)} $arg)"
        case AppliedType.List(tpe) =>
          val tpeEncoder = encoderDefinition(tpe, "", topLevel = false)
          s"(Encode.list $tpeEncoder) $arg"
        case AppliedType.Dict(tpe) =>
          val tpeEncoder = encoderDefinition(tpe, "", topLevel = false)
          s"(Encode.dict identity ($tpeEncoder) $arg)"
        case AppliedType.Result(_, _) =>
          "{- json encoding Result type not supported in endpoints-elm! -}"
      }
    case ReferencedType(name) =>
      s"Data.$name.encoder $arg"
    case TypeAlias(name, fields) if topLevel =>
      val fieldsEncoder = fields
        .map {
          case (nme, tpe) =>
            s"""( "$nme", ${encoderDefinition(tpe, s"$arg.$nme", topLevel = false)} )"""
        }
        .mkString("\n  [ ", "\n  , ", "\n  ]")

      List(
        s"Encode.object (fieldsEncoder $arg)",
        "",
        s"encoderTagged : (String, String) -> $name -> Encode.Value",
        s"""encoderTagged (discriminator, tag) $arg = Encode.object ((discriminator, Encode.string tag) :: fieldsEncoder $arg)""",
        "",
        s"fieldsEncoder : $name -> List (String, Encode.Value)",
        s"fieldsEncoder $arg = $fieldsEncoder"
      ).mkString("\n")

    case TypeAlias(name, _) =>
      encoderDefinition(ReferencedType(name), arg)

    case UnionType(name, constructors, discriminator) if topLevel =>
      constructors
        .flatMap {
          case (nme, tpe) =>
            Seq(
              s"  ${unionConstructorName(name, nme)} ${nme.toLowerCase} ->",
              s"""    Data.${tpe.name}.encoderTagged ("${discriminator
                .getOrElse("type")}", "$nme" ) ${nme.toLowerCase}""",
              ""
            )
        }
        .mkString(s"case $arg of\n", "\n", "")

    case UnionType(name, _, _) =>
      encoderDefinition(ReferencedType(name), arg)
  }

  def decoderDefinition(elmType: ElmType, topLevel: Boolean = true): String = elmType match {
    case basicType: BasicType =>
      basicType match {
        case cbt: CustomBasicType => s"(${cbt.decoderExpr})"
        case BasicType.Unit       => "Decode.succeed ()"
        case BasicType.String     => "Decode.string"
        case BasicType.Int        => "Decode.int"
        case BasicType.Float      => "Decode.float"
        case BasicType.Bool       => "Decode.bool"
        case BasicType.Uuid       => "Uuid.decoder"
        case BasicType.Bytes      => s"{- json decoding for Bytes not supported in endpoints-elm! -}"
      }
    case appliedType: AppliedType =>
      val decoder = appliedType match {
        case AppliedType.Maybe(tpe) =>
          s"Decode.maybe ${decoderDefinition(tpe, topLevel = false)}"
        case AppliedType.List(tpe) =>
          s"Decode.list ${decoderDefinition(tpe, topLevel = false)}"
        case AppliedType.Dict(tpe) =>
          s"Decode.dict ${decoderDefinition(tpe, topLevel = false)}"
        case AppliedType.Result(_, _) =>
          "{- json decoding Result type not supported in endpoints-elm! -}"
      }
      if (topLevel) decoder else s"($decoder)"
    case ReferencedType(name) =>
      s"Data.$name.decoder"
    case TypeAlias(name, fields) if topLevel =>
      fields
        .map {
          case (nme, AppliedType.Maybe(tpe)) =>
            s"""|> optional "$nme" (Decode.nullable ${decoderDefinition(tpe, topLevel = false)}) Nothing"""
          case (nme, tpe) =>
            s"""|> required "$nme" ${decoderDefinition(tpe, topLevel = false)} """
        }
        .mkString(s"Decode.succeed $name\n  ", "\n  ", "")

    case TypeAlias(name, _) =>
      decoderDefinition(ReferencedType(name))

    case UnionType(name, constructors, discriminator) if topLevel =>
      val cases = constructors
        .flatMap {
          case (nme, param) =>
            Seq(
              s"""  "$nme" ->""",
              s"    Decode.map ${unionConstructorName(name, nme)} ${decoderDefinition(param, topLevel = false)}",
              ""
            )
        }
        .mkString("\n")

      Seq(
        s"""Decode.field "${discriminator.getOrElse("type")}" Decode.string""",
        "  |> Decode.andThen decoderTagged",
        "",
        s"decoderTagged : String -> Decoder $name",
        "decoderTagged tag = case tag of",
        cases,
        "  _ ->",
        s"""    Decode.fail <| "Trying to decode $name, but type " ++ tag ++ " is not supported!""""
      ).mkString("\n")

    case UnionType(name, _, _) =>
      decoderDefinition(ReferencedType(name))
  }

  def imports(elmType: ElmType, topLevel: Boolean = true): Seq[String] = elmType match {
    case basicType: BasicType =>
      basicType match {
        case cbt: CustomBasicType => Seq(cbt.name)
        case BasicType.Uuid       => Seq("Uuid", "Random")
        case _                    => Nil
      }
    case appliedType: AppliedType =>
      val dict = if (appliedType.isInstanceOf[Dict]) Seq("Dict") else Nil
      dict ++ appliedType.args.flatMap(imports(_, topLevel = false))
    case rt: ReferencedType =>
      Seq(importModuleName(rt))
    case ta: TypeAlias if !topLevel =>
      Seq(importModuleName(ta))
    case TypeAlias(_, fields) =>
      fields.flatMap { case (_, tpe) => imports(tpe, topLevel = false) }
    case ut @ UnionType(_, constructors, _) if !topLevel =>
      importModuleName(ut) +: imports(constructors.head._2, topLevel = false)
    case UnionType(_, constructors, _) =>
      constructors.flatMap { case (_, param) => imports(param, topLevel = false) }
  }

  def importModuleName(elmType: ElmType): String = elmType match {
    case basicType: BasicType  => basicType.name
    case _: AppliedType        => ""
    case ReferencedType(name)  => s"Data.$name"
    case TypeAlias(name, _)    => s"Data.$name"
    case UnionType(name, _, _) => s"Data.$name"
  }

  def unionConstructorName(unionName: String, constructor: String): String =
    s"$unionName${constructor}Type"

  def recordSetters(elmType: ElmType): List[String] = elmType match {
    case TypeAlias(_, fields) =>
      fields.toList.flatMap {
        case (fieldName, fieldTpe) =>
          val tpeRef = ElmType.typeReference(elmType)
          val fieldTypeRef = ElmType.typeReference(fieldTpe)
          val argName = NameUtils.identFromTypeName(elmType)
          List(
            s"set${NameUtils.camelizeName(fieldName)} : $fieldTypeRef -> $tpeRef -> $tpeRef",
            s"set${NameUtils.camelizeName(fieldName)} new${NameUtils.camelizeName(fieldName)} $argName =",
            s"  { $argName | $fieldName = new${NameUtils.camelizeName(fieldName)} }",
            ""
          )
      }
    case _ =>
      Nil
  }

  def recordUpdaters(elmType: ElmType): List[String] = elmType match {
    case TypeAlias(_, fields) =>
      fields.toList.flatMap {
        case (fieldName, fieldTpe) =>
          val tpeRef = ElmType.typeReference(elmType)
          val fieldTypeRef = ElmType.typeReference(fieldTpe)
          val argName = NameUtils.identFromTypeName(elmType)
          List(
            s"update${NameUtils.camelizeName(fieldName)} : ($fieldTypeRef -> $fieldTypeRef) -> $tpeRef -> $tpeRef",
            s"update${NameUtils.camelizeName(fieldName)} f $argName =",
            s"  { $argName | $fieldName = f $argName.$fieldName }",
            ""
          )
      }
    case _ =>
      Nil
  }
}
