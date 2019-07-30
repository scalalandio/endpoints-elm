package io.scalaland.endpoints.elm.model

import io.scalaland.endpoints.elm.emit.{NameUtils, TypeEmit}

case class ElmEndpoint(name: String,
                       request: ElmRequest,
                       responseEncoding: ElmEntityEncoding,
                       response: ElmType,
                       summary: Option[String],
                       description: Option[String],
                       tags: List[String])

case class ElmRequest(method: String,
                      url: ElmUrl,
                      encoding: ElmEntityEncoding,
                      entity: ElmType,
                      headers: List[ElmHeader]) {

  def name: String = {
    val urlSegments = url.segments.collect {
      case StaticSegment(n)      => n
      case VariableSegment(n, _) => n
    }
    NameUtils.identNameFromSegments(urlSegments :+ method)
  }
}

case class ElmUrl(segments: List[ElmUrlSegment], queryParams: List[(String, ElmType)]) {

  def segmentTpes: List[(String, String)] = {
    segments.collect {
      case VariableSegment(nme, tpe) => nme -> TypeEmit.typeReference(tpe, topLevel = false)
    }
  }

  def queryParamTpes: List[(String, String)] = {
    queryParams.map {
      case (arg, tpe) => arg -> TypeEmit.typeReference(tpe, topLevel = false)
    }
  }
}

sealed trait ElmUrlSegment
case class StaticSegment(segment: String) extends ElmUrlSegment
case class VariableSegment(name: String, tpe: ElmType) extends ElmUrlSegment

sealed trait ElmHeader {
  def name: String
  def normalizedName: String = NameUtils.identNameFromSegments(name.split("-"))
}

case class RequiredHeader(name: String) extends ElmHeader
case class OptionalHeader(name: String) extends ElmHeader

sealed trait ElmEntityEncoding
case object NoEntity extends ElmEntityEncoding
case object StringEncoding extends ElmEntityEncoding
case object JsonEncoding extends ElmEntityEncoding
case class BinaryEncoding(contentType: String) extends ElmEntityEncoding
