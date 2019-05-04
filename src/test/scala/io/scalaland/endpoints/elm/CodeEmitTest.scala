package io.scalaland.endpoints.elm

import TestElmEndpoints._

object CodeEmitTest extends App {

  args match {
    case Array(_, outputDir) =>

      println(s"Generating code to $outputDir...")

      writeElmCode(outputDir)(currentValue, increment)()

    case _ =>
      println("usage: CodeEmitTest [output_dir]")
  }

}
