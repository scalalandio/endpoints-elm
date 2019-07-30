package io.scalaland.endpoints.elm

object CodeEmitTest extends App with ElmCodeGenerator {

  args match {
    case Array(outputDir) =>
      println(s"Generating code to $outputDir...")

      val allEndpoints =
        CounterTest.TestElmEndpoints.allEndpoints ++
          SegmentsTest.TestElmEndpoints.allEndpoints ++
          QueryParamsTest.TestElmEndpoints.allEndpoints ++
          RequestsTest.TestElmEndpoints.allEndpoints ++
          ResponsesTest.TestElmEndpoints.allEndpoints

      writeElmCode(outputDir)(allEndpoints: _*)(httpApiUrlPrefix = "/this-is/test")

    case _ =>
      println("usage: CodeEmitTest [output_dir]")
  }

}
