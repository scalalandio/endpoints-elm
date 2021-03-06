package io.scalaland.endpoints.elm.emit

import io.scalaland.endpoints.elm.BuildInfo
import io.scalaland.endpoints.elm.model.{BasicType, CustomBasicType, ElmEndpoint, ElmType}

object Commons {

  val headerComment: String =
    s"""{-
      |  This file was generated by endpoints-elm ${BuildInfo.version} interpreter.
      |  Do not edit this file manually.
      |
      |  See https://github.com/scalalandio/endpoints-elm for more information.
      |-}
      |""".stripMargin

  def documentationString(endpoint: ElmEndpoint): String = {
    (endpoint.summary, endpoint.description) match {
      case (Some(summaryDoc), Some(descriptionDoc)) =>
        s"{-| $summaryDoc\n\n$descriptionDoc\n-}"
      case (Some(summaryDoc), None) =>
        s"{-| $summaryDoc -}"
      case (None, Some(descriptionDoc)) =>
        s"{-| $descriptionDoc -}"
      case (None, None) =>
        ""
    }
  }

  def toStringFunctionExpr(elmType: ElmType): Option[String] = elmType match {
    case BasicType.Uuid       => Some("Uuid.toString")
    case BasicType.String     => None
    case BasicType.Bool       => Some("(String.toLower << Bool.Extra.toString)")
    case BasicType.Int        => Some("String.fromInt")
    case BasicType.Float      => Some("String.fromFloat")
    case cbt: CustomBasicType => Some(cbt.toStringExpr)
    case _                    => Some("toString")
  }
}
