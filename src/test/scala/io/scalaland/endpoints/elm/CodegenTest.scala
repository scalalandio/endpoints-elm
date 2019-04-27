package io.scalaland.endpoints.elm

import java.io.File

import utest._
import TestEndpoints._

import scala.io.Source

object CodegenTest extends TestSuite {

  val tests = Tests {

    "generate code for simple domain model" - {

      generateElmContents(currentValue, increment)() ==> Seq(
        new File("Data/Counter.elm") ->
          Source.fromResource("test-code/Data/Counter.elm").getLines().mkString("\n"),
        new File("Data/Increment.elm") ->
          Source.fromResource("test-code/Data/Increment.elm").getLines().mkString("\n"),
        new File("Request/Counter.elm") ->
          Source.fromResource("test-code/Request/Counter.elm").getLines().mkString("\n")
      )
    }
  }
}
