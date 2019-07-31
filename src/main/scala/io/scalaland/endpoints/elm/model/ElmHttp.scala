package io.scalaland.endpoints.elm.model

import io.scalaland.endpoints.elm.emit.{NameUtils, TypeEmit}

case class ElmEndpoint(name: String,
                       request: ElmRequest,
                       encodedType: EncodedType,
                       summary: Option[String],
                       description: Option[String],
                       tags: List[String])

case class ElmRequest(method: String, url: ElmUrl, encodedType: EncodedType, headers: List[ElmHeader]) {

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
      case VariableSegment(nme, tpe) => nme -> ElmType.typeReference(tpe, topLevel = false)
    }
  }

  def queryParamTpes: List[(String, String)] = {
    queryParams.map {
      case (arg, tpe) => arg -> ElmType.typeReference(tpe, topLevel = false)
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

sealed trait EncodedType {
  def tpe: ElmType
  def contentType: String

  def resolveExpr: String // elm expr of type: Http.Response respEnc -> Result Http.Error tpe
  def resolverFunction
    : String // elm expr of type: (Http.Response respEnc -> Result Http.Error a) -> Http.Resolver Http.Error a
  def encodeBody(argName: String): String // elm expr of type Body that may consume argName of type tpe
}

case object NoEntityEncodedType extends EncodedType {
  def tpe: ElmType = BasicType.Unit
  def contentType: String = "text/plain"
  def resolveExpr: String = "EndpointsElm.httpResolveUnit"
  def resolverFunction: String = "Http.stringResolver"
  def encodeBody(argName: String): String = s"""Http.stringBody "$contentType" "" """
}

case object StringEncodedType extends EncodedType {
  def tpe: ElmType = BasicType.String
  def contentType: String = "text/plain"
  def resolveExpr: String = s"EndpointsElm.httpResolveString"
  def resolverFunction: String = "Http.stringResolver"
  def encodeBody(argName: String): String = s"""Http.stringBody "$contentType" $argName"""
}

case class JsonEncodedType(tpe: ElmType) extends EncodedType {
  def contentType: String = "application/json"
  def resolveExpr: String = s"EndpointsElm.httpResolveJson (${TypeEmit.decoderDefinition(tpe, topLevel = false)})"
  def resolverFunction: String = "Http.stringResolver"
  def encodeBody(argName: String): String =
    s"""Http.jsonBody (${TypeEmit.encoderDefinition(tpe, argName, topLevel = false)})"""
}

case class BinaryEncodedType(tpe: ElmType = BasicType.Bytes) extends EncodedType {
  def contentType: String = "application/octet-stream"
  def resolveExpr: String = "EndpointsElm.httpResolveBytes"
  def resolverFunction: String = "Http.bytesResolver"
  def encodeBody(argName: String): String = s"""Http.bytesBody "$contentType" $argName"""
}

class WrappedEncodedType(underlying: EncodedType) extends EncodedType {
  def tpe: ElmType = underlying.tpe
  def contentType: String = underlying.contentType
  def resolveExpr: String = underlying.resolveExpr
  def resolverFunction: String = underlying.resolverFunction
  def encodeBody(argName: String): String = underlying.encodeBody(argName)
}
