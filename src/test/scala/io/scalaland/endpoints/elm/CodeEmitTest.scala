package io.scalaland.endpoints.elm

object CodeEmitTest extends App with ElmCodeGenerator {

  args match {
    case Array(_, outputDir) =>
      println(s"Generating code to $outputDir...")

      val allEndpoints =
        CounterTest.TestElmEndpoints.allEndpoints ++
          SegmentsTest.TestElmEndpoints.allEndpoints ++
          QueryParamsTest.TestElmEndpoints.allEndpoints

      writeElmCode(outputDir)(allEndpoints: _*)()

    case _ =>
      println("usage: CodeEmitTest [output_dir]")
  }

}
