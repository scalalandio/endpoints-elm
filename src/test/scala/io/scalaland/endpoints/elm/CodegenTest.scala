package io.scalaland.endpoints.elm

import java.io.File

import utest._

trait CodegenTest extends TestSuite {

  type Result = Seq[(File, String)]

  implicit class ResultOps(val result: Result) {

    def sameAs(other: Result): Unit = {
      val given = result.toMap
      val expected = other.toMap

      Predef.assert(
        given.keySet == expected.keySet,
        s"Expected to made of the same files - A-B=${given.keySet -- expected.keySet}, B-A=${expected.keySet -- given.keySet}"
      )

      def diff(left: String, right: String) = {
        val leftLines = left.split('\n')
        val rightLines = right.split('\n')
        leftLines.zip(rightLines).zipWithIndex.collect {
          case ((l, r), i) if l != r =>
            s"$i: $l != $r"
        } ++ {
          val (isLeft, bigger) =
            if (leftLines.length >= rightLines.length) true -> leftLines
            else false -> rightLines
          bigger.zipWithIndex
            .drop(leftLines.length min rightLines.length)
            .map {
              case (s, i) => s"$i: $s ${if (isLeft) "left" else "right"} only"
            }
        }
      }

      given.keySet foreach { file =>
      val difference = diff(given(file), expected(file))
        Predef.assert(difference.isEmpty, s"Difference at $file:\n ${difference.mkString("\n")}")
      }
    }
  }
}
