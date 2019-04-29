package io.scalaland.endpoints.elm

import java.util.UUID

import utest._

import scala.util.Try

object BasicTypeTest extends TestSuite {

  object Domain {

    import endpoints.{algebra, generic}

    trait TestJsonSchemas extends generic.JsonSchemas {}

    trait TestEndpoints
        extends algebra.Endpoints
        with algebra.JsonSchemaEntities
        with algebra.JsonSchemas
        with TestJsonSchemas {

      implicit val floatSegment: Segment[Float] = refineSegment(stringSegment)(s => Try(s.toFloat).toOption)(_.toString)
      implicit val doubleSegment: Segment[Double] = refineSegment(stringSegment)(s => Try(s.toDouble).toOption)(_.toString)
      implicit val booleanSegment: Segment[Boolean] = refineSegment(stringSegment)(s => Try(s.toBoolean).toOption)(_.toString)

      val unitEndpoint: Endpoint[Unit, Unit] =
        endpoint(get(path / "unit-echo"), emptyResponse(docs = Some("Unit echo")), tags = List("BasicType"))
      val stringEndpoint: Endpoint[String, String] =
        endpoint(get(path / "string-echo" / segment[String]()), jsonResponse[String](docs = Some("String echo")), tags = List("BasicType"))
      val intEndpoint: Endpoint[Int, Int] =
        endpoint(get(path / "int-echo" / segment[Int]()), jsonResponse[Int](docs = Some("Int echo")), tags = List("BasicType"))
      val longEndpoint: Endpoint[Long, Long] =
        endpoint(get(path / "long-echo" / segment[Long]()), jsonResponse[Long](docs = Some("Long echo")), tags = List("BasicType"))
      val floatEndpoint: Endpoint[Float, Float] =
        endpoint(get(path / "float-echo" /  segment[Float]()), jsonResponse[Float](docs = Some("Float echo")), tags = List("BasicType"))
      val doubleEndpoint: Endpoint[Double, Double] =
        endpoint(get(path / "double-echo" /  segment[Double]()), jsonResponse[Double](docs = Some("Double echo")), tags = List("BasicType"))
      val booleanEndpoint: Endpoint[Boolean, Boolean] =
        endpoint(get(path / "boolean-echo" /  segment[Boolean]()), jsonResponse[Boolean](docs = Some("Boolean echo")), tags = List("BasicType"))
      val uuidEndpoint: Endpoint[UUID, UUID] =
        endpoint(get(path / "uuid-echo" /  segment[UUID]()), jsonResponse[UUID](docs = Some("UUID echo")), tags = List("BasicType"))

    }

    object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator
  }

  val tests = Tests {
    import Domain.TestElmEndpoints._

    "generate code for simple domain model" - {

      generateElmContents(
        unitEndpoint,
        stringEndpoint,
        intEndpoint,
        longEndpoint,
        floatEndpoint,
        doubleEndpoint,
        booleanEndpoint,
        uuidEndpoint
      )() ==>
        ReferenceData.from("basic-type-test")(
          "Request/BasicType.elm"
        )
    }
  }
}
