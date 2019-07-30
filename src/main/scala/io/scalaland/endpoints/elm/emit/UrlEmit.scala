package io.scalaland.endpoints.elm.emit

import io.scalaland.endpoints.elm.model._

object UrlEmit {

  def moduleDefinition(module: ElmHttpModule)(implicit ctx: Context): String = {
    (List(
      Commons.headerComment,
      s"module Request.Url.${module.name} exposing (..)",
      "",
      urlImports,
      imports(module).distinct.sorted.map(i => s"import $i exposing (..)").mkString("\n"),
      "",
    ) ++ module.endpoints.flatMap(urlDefinition))
      .mkString("\n")
  }

  private val urlImports =
    s"""import Url.Builder
       |import Bool.Extra
       |import Maybe.Extra
       |
       |""".stripMargin

  def urlDefinition(endpoint: ElmEndpoint)(implicit ctx: Context): Seq[String] = {

    val (qpArgs, qpTpes) = endpoint.request.url.queryParams.map {
      case (arg, tpe) => arg -> TypeEmit.typeReference(tpe, topLevel = false)
    }.unzip

    val (urlArgs, urlTpes) = endpoint.request.url.segments.collect {
      case VariableSegment(nme, tpe) => nme -> TypeEmit.typeReference(tpe, topLevel = false)
    }.unzip

    val argNames = urlArgs ++ qpArgs
    val argTypesStr = urlTpes ++ qpTpes

    List(
      Commons.documentationString(endpoint),
      s"${endpoint.name} : ${(argTypesStr :+ "String").mkString(" -> ")}",
      s"${endpoint.name} ${argNames.mkString(" ")} =",
      s"""${urlStringLiteral(ctx.urlPrefix)(endpoint.request.url)}""",
      ""
    )
  }

  def urlStringLiteral(urlPrefix: String)(elmUrl: ElmUrl): String = {

    val prefix = if(urlPrefix.isEmpty) "/" else urlPrefix

    List(
      "  Url.Builder.relative",
      s"""    ["$prefix"${urlSegmentExprs(elmUrl).mkString(", ", ", ", "")}]""",
      s"    ${urlQueryParamsExprs(elmUrl).mkString("(", " ++ ", ")")}"
    ).mkString("\n")
  }

  def urlSegmentExprs(elmUrl: ElmUrl): Seq[String] = {
    elmUrl.segments.collect {
      case StaticSegment(part) if part.nonEmpty =>
        "\"" + part + "\""
      case VariableSegment(name, tpe) =>
        Commons.toStringFunctionExpr(tpe)
          .map(toString => s"$toString $name")
          .getOrElse(name)
    }
  }

  def urlQueryParamsExprs(elmUrl: ElmUrl): Seq[String] = {
    val paramsExprs = elmUrl.queryParams
      .map { case (qpName, qpTpe) => toQueryParamsListElems(qpName, qpTpe) }

    if(paramsExprs.nonEmpty) paramsExprs else Seq("[]")
  }


  def toQueryParamsListElems(queryParamName: String, elmType: ElmType): String = elmType match {
    case AppliedType.Maybe(tpe) =>
      s"""($queryParamName |> Maybe.map (\\p -> Url.Builder.string "$queryParamName" (${Commons.toStringFunctionExpr(tpe).getOrElse("")} p)) |> Maybe.Extra.toList)"""
    case AppliedType.List(tpe) =>
      s"""($queryParamName |> List.map (\\p -> Url.Builder.string "$queryParamName" (${Commons.toStringFunctionExpr(tpe).getOrElse("")} p)))""".stripMargin
    case BasicType.Int =>
      s"""[Url.Builder.int "$queryParamName" $queryParamName]"""
    case _: BasicType =>
      s"""[Url.Builder.string "$queryParamName" (${Commons.toStringFunctionExpr(elmType).getOrElse("")} $queryParamName)]""".stripMargin
    case _ =>
      s"[{- unsupported query params type in elm codegen: $elmType -}]"
  }


  def imports(module: ElmHttpModule): Seq[String] = {
    module.endpoints
      .flatMap(urlReferencedTypes)
      .flatMap(ElmType.referencesShallow)
      .map(TypeEmit.importModuleName)
  }

  def urlReferencedTypes(endpoint: ElmEndpoint): Seq[ElmType] = {
    val segmentTpes = endpoint.request.url.segments.collect { case VariableSegment(_, tpe) => tpe }.distinct
    val queryParamTpes = endpoint.request.url.queryParams.map(_._2).distinct
    segmentTpes ++ queryParamTpes
  }

}
