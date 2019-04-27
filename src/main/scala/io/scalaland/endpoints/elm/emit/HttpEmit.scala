package io.scalaland.endpoints.elm.emit

import io.scalaland.endpoints.elm.model._

object HttpEmit {

  def moduleDefinition(module: ElmHttpModule)(implicit ctx: Context): String = {
    (List(
      Commons.headerComment,
      s"module Request.${module.name} exposing (..)",
      "",
      httpImports,
      imports(module).distinct.sorted.map(i => s"import $i exposing (..)").mkString("\n"),
      "",
    ) ++ module.endpoints.flatMap(endpointDefinition))
      .mkString("\n")
  }

  private val httpImports =
    s"""import Http
       |import HttpBuilder exposing (RequestBuilder)
       |import Json.Decode as Decode
       |import Bool.Extra
       |import Maybe.Extra
       |""".stripMargin

  def endpointDefinition(endpoint: ElmEndpoint)(implicit ctx: Context): Seq[String] = {

    val (qpArgs, qpTpes) = endpoint.request.url.queryParams.map {
      case (arg, tpe) => arg -> TypeEmit.typeReference(tpe, topLevel = false)
    }.unzip

    val (urlArgs, urlTpes) = endpoint.request.url.segments.collect {
      case VariableSegment(nme, tpe) => nme -> TypeEmit.typeReference(tpe, topLevel = false)
    }.unzip

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

    val argNames = urlArgs ++ qpArgs ++ bodyArgs ++ headerArgs

    val argTypesStr = urlTpes ++ qpTpes ++ bodyTypesStr ++ headerTpes
    val retTpeStr = s"RequestBuilder ${TypeEmit.typeReference(endpoint.response, topLevel = false)}"

    val withQueryParamsModifier = if (endpoint.request.url.queryParams.isEmpty) {
      Nil
    } else {

      val elmQueryParamsList = endpoint.request.url.queryParams.map {
        case (qpArgName, qpTpe) =>
          toQueryParamsListElems(qpArgName, qpTpe)
      }

      List(s"""HttpBuilder.withQueryParams ${elmQueryParamsList.mkString("(", " ++ ", ")")}""")
    }

    val withHeaderModifiers = endpoint.request.headers.map { header =>
      s"""HttpBuilder.withHeader "${header.name}" ${header.normalizedName}"""
    }

    val withCredentialsModifier = if (ctx.withCredentials) List("HttpBuilder.withCredentials") else List.empty[String]

    val withModifiers =
      withQueryParamsModifier ++
        withBodyModifier ++
        withCredentialsModifier ++
        withHeaderModifiers :+
        responseExpect(endpoint) :+
        "HttpBuilder.withTimeout 30000"

    List(
      documentationString(endpoint),
      s"${endpoint.name} : ${(argTypesStr :+ retTpeStr).mkString(" -> ")}",
      s"${endpoint.name} ${argNames.mkString(" ")} =",
      s"""  HttpBuilder.${endpoint.request.method} ${urlStringLiteral(ctx.urlPrefix)(endpoint.request.url)}""",
      withModifiers.mkString("    |> ", "\n    |> ", ""),
      ""
    )
  }

  def urlStringLiteral(urlPrefix: String)(elmUrl: ElmUrl): String = {

    val urlExpr = elmUrl.segments
      .collect {
        case StaticSegment(s) =>
          s
        case VariableSegment(nme, tpe) =>
          val stringArg = toStringFunctionName(tpe)
            .map(toString => s"$toString $nme")
            .getOrElse(nme)
          s"""" ++ $stringArg ++ """"
      }
      .mkString("\"" + urlPrefix, "/", "\"")
      .replace(" ++ \"\"", "")

    if (elmUrl.segments.exists(_.isInstanceOf[VariableSegment])) {
      s"($urlExpr)"
    } else {
      urlExpr
    }
  }

  def toStringFunctionName(elmType: ElmType): Option[String] = elmType match {
    case BasicType.Uuid   => Some("Uuid.toString")
    case BasicType.String => None
    case BasicType.Bool   => Some("(String.toLower << Bool.Extra.toString)")
    case BasicType.Int    => Some("String.fromInt")
    case _                => Some("toString")
  }

  def toQueryParamsListElems(queryParamName: String, elmType: ElmType): String = elmType match {
    case AppliedType.Maybe(tpe) =>
      s"""($queryParamName |> Maybe.map (\\p -> ("$queryParamName", ${toStringFunctionName(tpe)
        .getOrElse("")} p)) |> Maybe.Extra.toList)"""
    case AppliedType.List(tpe) =>
      s"""($queryParamName |> List.map (\\p -> ("$queryParamName", ${toStringFunctionName(tpe).getOrElse("")} p))"""
    case _: BasicType =>
      s"""[("$queryParamName", ${toStringFunctionName(elmType).getOrElse("")} $queryParamName)]"""
    case _ =>
      s"[{- unsupported query params type in elm codegen: $elmType -}]"
  }

  def requestEntity(elmRequest: ElmRequest): Option[(String, String, String)] = {
    elmRequest.encoding match {
      case NoEntity =>
        None
      case StringEncoding =>
        Some(("body", "String", """HttpBuilder.withStringBody "text/plain" body"""))
      case JsonEncoding =>
        val argName = NameUtils.identFromTypeName(elmRequest.entity)
        Some(
          (
            argName,
            elmRequest.entity.name,
            s"HttpBuilder.withJsonBody (${TypeEmit.encoderDefinition(elmRequest.entity, "", topLevel = false)} $argName)"
          )
        )
    }
  }

  def responseExpect(elmEndpoint: ElmEndpoint): String = {
    elmEndpoint.responseEncoding match {
      case NoEntity =>
        "HttpBuilder.withExpect (Http.expectStringResponse (\\_ -> Ok ()))"
      case StringEncoding =>
        "HttpBuilder.withExpectString"
      case JsonEncoding =>
        s"HttpBuilder.withExpectJson ${TypeEmit.decoderDefinition(elmEndpoint.response, topLevel = false)}"
    }
  }

  def imports(module: ElmHttpModule): Seq[String] = {
    module.endpoints
      .flatMap(endpointReferencedTypes)
      .flatMap(ElmType.referencesShallow)
      .map(TypeEmit.importModuleName)
  }

  def endpointReferencedTypes(endpoint: ElmEndpoint): Seq[ElmType] = {
    val urlArgTpes = endpoint.request.url.segments.collect {
      case VariableSegment(_, tpe) => tpe
    }
    urlArgTpes :+ endpoint.request.entity :+ endpoint.response
  }

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
}
