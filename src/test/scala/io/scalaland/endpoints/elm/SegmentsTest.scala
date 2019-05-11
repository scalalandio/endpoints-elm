package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.model.{ElmEndpoint, ElmType}
import utest._

object SegmentsTest extends CodegenTest {

  import endpoints.algebra

  trait TestEndpoints
    extends algebra.Endpoints
      with algebra.JsonSchemaEntities
      with algebra.JsonSchemas {

    implicit def dateSegment: Segment[LocalDate]

    def stringEndpoint: Endpoint[String, Unit] =
      endpoint(
        get(path / "string" / segment[String]("string")),
        emptyResponse(),
        tags = List("Segments")
      )

    def intEndpoint: Endpoint[Int, Unit] =
      endpoint(
        get(path / "int" / segment[Int]("int")),
        emptyResponse(),
        tags = List("Segments")
      )

    def longEndpoint: Endpoint[Long, Unit] =
      endpoint(
        get(path / "long" / segment[Long]("long")),
        emptyResponse(),
        tags = List("Segments")
      )

    def uuidEndpoint: Endpoint[UUID, Unit] =
      endpoint(
        get(path / "uuid" / segment[UUID]("uuid")),
        emptyResponse(),
        tags = List("Segments")
      )

    def dateEndpoint: Endpoint[LocalDate, Unit] =
      endpoint(
        get(path / "date" / segment[LocalDate]("date")),
        emptyResponse(),
        tags = List("Segments")
      )
  }

  object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator {

    implicit def dateSegment: ElmType = CustomTypes.date

    val allEndpoints: Seq[ElmEndpoint] = Seq(
      stringEndpoint,
      intEndpoint,
      longEndpoint,
      uuidEndpoint,
      dateEndpoint
    )
  }

  val tests = Tests {
    import TestElmEndpoints._

    "support segments" - {

      generateElmContents(allEndpoints: _*)() sameAs ReferenceData.from("segments-test")(
        "Request/Segments.elm"
      )
    }
  }

}
