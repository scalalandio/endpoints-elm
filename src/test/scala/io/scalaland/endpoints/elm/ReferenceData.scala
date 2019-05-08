package io.scalaland.endpoints.elm

import java.io.{File, PrintWriter}

import scala.io.Source

object ReferenceData {

  def from(resourceDir: String)(genFileNames: String*): Seq[(File, String)] = genFileNames.map { genFileName =>
    new File(genFileName) ->
      Source.fromResource(s"$resourceDir/$genFileName").getLines.mkString("\n")
  }

  def save(outputDir: String)(data: Seq[(File, String)]): Unit = data.foreach {
    case (file, content) =>
      val target = new File(s"$outputDir/${file.getPath}")
      target.getParentFile.mkdirs()
      target.createNewFile()
      new PrintWriter(s"$outputDir/${file.getPath}") { write(content); close() }
  }
}
