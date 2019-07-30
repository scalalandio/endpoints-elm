package io.scalaland.endpoints.elm.emit

import io.scalaland.endpoints.elm.model._

object HttpEmit {

  def moduleDefinition(module: ElmHttpModule)(implicit ctx: Context): String = {
    (List(
      Commons.headerComment,
      s"module Request.${module.name} exposing (..)",
      "",
      s"import Request.Url.${module.name}",
      httpImports,
      imports(module).distinct.sorted.map(i => s"import $i exposing (..)").mkString("\n"),
      "",
    ) ++ module.endpoints.flatMap(endpointDefinition(module.name)))
      .mkString("\n")
  }

  private val httpImports =
    s"""import Http
       |import HttpBuilder.Task exposing (RequestBuilder)
       |import Json.Decode as Decode
       |import Json.Encode as Encode
       |import Bool.Extra
       |import Maybe.Extra
       |import Bytes exposing (Bytes)
       |import Dict exposing (Dict)
       |""".stripMargin

  def endpointDefinition(moduleName: String)(endpoint: ElmEndpoint)(implicit ctx: Context): Seq[String] = {

    val (segmentArgs, segmentTpes) = endpoint.request.url.segmentTpes.unzip
    val (qpArgs, qpTpes) = endpoint.request.url.queryParamTpes.unzip

    val headerArgs = endpoint.request.headers.map(_.normalizedName)
    val headerTpes = endpoint.request.headers.map {
      case _: RequiredHeader => "String"
      case _: OptionalHeader => "Maybe String"
    }

    val (bodyArgs, bodyTypesStr, withBodyModifier) = requestEntity(endpoint.request) match {
      case Some((bodyArg, bodyTpe, withBody)) =>
        (List(bodyArg), List(bodyTpe), List(withBody))
      case None =>
        (Nil, Nil, Nil)
    }

    val argNames = segmentArgs ++ qpArgs ++ bodyArgs ++ headerArgs
    val argTypesStr = segmentTpes ++ qpTpes ++ bodyTypesStr ++ headerTpes
    val retTpeStr = s"RequestBuilder Http.Error ${TypeEmit.typeReference(endpoint.response, topLevel = false)}"

    val withHeaderModifiers = endpoint.request.headers.map { header =>
      s"""HttpBuilder.Task.withHeader "${header.name}" ${header.normalizedName}"""
    }

    val withCredentialsModifier = if (ctx.withCredentials) List("HttpBuilder.Task.withCredentials") else List.empty[String]

    val withModifiers =
      withBodyModifier ++
        withCredentialsModifier ++
        withHeaderModifiers :+
        responseExpect(endpoint) :+
        "HttpBuilder.Task.withTimeout 30000"

    val urlExpr = s"(Request.Url.$moduleName.${endpoint.name} ${(segmentArgs ++ qpArgs).mkString(" ")})"

    List(
      Commons.documentationString(endpoint),
      s"${endpoint.name} : ${(argTypesStr :+ resolverTpe(endpoint) :+ retTpeStr).mkString(" -> ")}",
      s"${endpoint.name} ${argNames.mkString(" ")} $resolverValName =",
      s"""  HttpBuilder.Task.${endpoint.request.method} $urlExpr""".stripMargin,
      withModifiers.mkString("    |> ", "\n    |> ", ""),
      ""
    )
  }

  def requestEntity(elmRequest: ElmRequest): Option[(String, String, String)] = {
    elmRequest.encoding match {
      case NoEntity =>
        None
      case StringEncoding =>
        Some(("body", "String", """HttpBuilder.Task.withBody (Http.stringBody "text/plain" body)"""))
      case JsonEncoding =>
        val argName = NameUtils.identFromTypeName(elmRequest.entity)
        Some(
          (
            argName,
            ElmType.tpeSignature(elmRequest.entity),
            s"HttpBuilder.Task.withBody (Http.jsonBody (${TypeEmit.encoderDefinition(elmRequest.entity, "", topLevel = false)} $argName))"
          )
        )
      case BinaryEncoding(contentType) =>
        Some(
          (
            "bytes",
            "Bytes",
            s"""HttpBuilder.Task.withBody (Http.bytesBody "$contentType" bytes)""".stripMargin
          )
        )
    }
  }

  def responseExpect(elmEndpoint: ElmEndpoint): String = {
    elmEndpoint.responseEncoding match {
      case NoEntity =>
        s"HttpBuilder.Task.withResolver (Http.stringResolver ($resolverValName >> Result.map (\\_ -> ())))"
      case StringEncoding =>
        s"HttpBuilder.Task.withResolver (Http.stringResolver $resolverValName)"
      case JsonEncoding =>
        val jsonDecoder = TypeEmit.decoderDefinition(elmEndpoint.response, topLevel = false)
        val jsonResolver = s"$resolverValName >> Result.andThen (Decode.decodeString $jsonDecoder >> Result.mapError (Decode.errorToString >> Http.BadBody))"
        s"HttpBuilder.Task.withResolver (Http.stringResolver ($jsonResolver))"
      case BinaryEncoding(_) =>
        s"HttpBuilder.Task.withResolver (Http.bytesResolver $resolverValName)"
    }
  }

  def resolverTpe(endpoint: ElmEndpoint): String = {
    val responseTpe = endpoint.responseEncoding match {
      case BinaryEncoding(_) =>
        "Bytes"
      case _ =>
        "String"
    }
    s"(Http.Response $responseTpe -> Result Http.Error $responseTpe)"
  }

  def imports(module: ElmHttpModule): Seq[String] = {
    module.endpoints
      .flatMap(endpointReferencedTypes)
      .flatMap(ElmType.referencesShallow)
      .map(TypeEmit.importModuleName)
  }

  def endpointReferencedTypes(endpoint: ElmEndpoint): Seq[ElmType] = {
    val segmentTpes = endpoint.request.url.segments.collect { case VariableSegment(_, tpe) => tpe }.distinct
    val queryParamTpes = endpoint.request.url.queryParams.map(_._2).distinct
    segmentTpes ++ queryParamTpes :+ endpoint.request.entity :+ endpoint.response
  }

  private val resolverValName = "resolver__"
}
