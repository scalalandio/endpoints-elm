package io.scalaland.endpoints.elm.emit

import io.scalaland.endpoints.elm.model._

object NameUtils {

  def identNameFromSegments(segments: Seq[String]): String = {
    val nonEmptySegments = segments.filter(_.nonEmpty).map(camelizeName)
    nonEmptySegments.head.toLowerCase + nonEmptySegments.tail.map(_.capitalize).mkString
  }

  def identFromTypeName(elmType: ElmType): String = {
    elmType.name.head.toLower + elmType.name.tail
  }

  def camelizeName(name: String): String = {
    name.split("[-\\./]").map(_.capitalize).mkString
  }
}
