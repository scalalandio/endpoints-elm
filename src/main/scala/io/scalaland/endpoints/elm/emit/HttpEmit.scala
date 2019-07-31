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
      imports(module).map(i => s"import $i exposing (..)").mkString("\n"),
      extraImports(module).map(i => s"import $i").mkString("\n"),
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
    val retTpeStr = s"RequestBuilder Http.Error ${ElmType.typeReference(endpoint.encodedType.tpe, topLevel = false)}"

    // TODO: add some tests to test headers
    val withHeaderModifiers = endpoint.request.headers.map { header =>
      s"""HttpBuilder.Task.withHeader "${header.name}" ${header.normalizedName}"""
    }

    val withCredentialsModifier =
      if (ctx.withCredentials) List("HttpBuilder.Task.withCredentials") else List.empty[String]

    val withModifiers =
      withBodyModifier ++
        withCredentialsModifier ++
        withHeaderModifiers :+
        responseResolver(endpoint) :+
        "HttpBuilder.Task.withTimeout 30000"

    val urlExpr = s"(Request.Url.$moduleName.${endpoint.name} ${(segmentArgs ++ qpArgs).mkString(" ")})"

    List(
      Commons.documentationString(endpoint),
      s"${endpoint.name} : ${(argTypesStr :+ retTpeStr).mkString(" -> ")}",
      s"${endpoint.name} ${argNames.mkString(" ")} =",
      s"""  HttpBuilder.Task.${endpoint.request.method} $urlExpr""".stripMargin,
      withModifiers.mkString("    |> ", "\n    |> ", ""),
      ""
    )
  }

  def requestEntity(elmRequest: ElmRequest): Option[(String, String, String)] = {
    elmRequest.encodedType match {
      case NoEntityEncodedType =>
        None
      case et =>
        val argName = NameUtils.identFromTypeName(et.tpe)
        Some {
          (argName, ElmType.tpeSignature(et.tpe), s"HttpBuilder.Task.withBody (${et.encodeBody(argName)})")
        }
    }
  }

  def responseResolver(elmEndpoint: ElmEndpoint): String = {
    def et = elmEndpoint.encodedType
    s"HttpBuilder.Task.withResolver (${et.resolverFunction} (${et.resolveExpr}))"
  }

  def imports(module: ElmHttpModule): Seq[String] = {
    module.endpoints
      .flatMap(endpointReferencedTypes)
      .flatMap(ElmType.referencesShallow)
      .map(TypeEmit.importModuleName)
      .distinct
      .sorted
  }

  def extraImports(module: ElmHttpModule): Seq[String] = {
    module.endpoints
      .flatMap { endpoint =>
        endpoint.encodedType.extraImports ++ endpoint.request.encodedType.extraImports
      }
      .distinct
      .sorted
  }

  def endpointReferencedTypes(endpoint: ElmEndpoint): Seq[ElmType] = {
    val segmentTpes = endpoint.request.url.segments.collect { case VariableSegment(_, tpe) => tpe }.distinct
    val queryParamTpes = endpoint.request.url.queryParams.map(_._2).distinct
    segmentTpes ++ queryParamTpes :+ endpoint.request.encodedType.tpe :+ endpoint.encodedType.tpe
  }
}
