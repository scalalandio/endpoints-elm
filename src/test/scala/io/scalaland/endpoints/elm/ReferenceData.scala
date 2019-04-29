package io.scalaland.endpoints.elm

import java.io.File

import scala.io.Source

object ReferenceData {

  def from(resourceDir: String)(genFileNames: String*): Seq[(File, String)] = genFileNames.map { genFileName =>
      new File(genFileName) ->
        Source.fromResource(s"$resourceDir/$genFileName").getLines.mkString("\n")
    }
}