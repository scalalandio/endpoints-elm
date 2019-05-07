package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.model.{CustomBasicType, ElmType}
import utest._

object BasicTypeTest extends CodegenTest {

  object Domain {

    import endpoints.{algebra, generic}

    sealed trait TimeOrID
    object TimeOrID {
      final case class Time(value: LocalDate, msg: String) extends TimeOrID
      final case class ID(value: UUID, msg: String) extends TimeOrID
    }

    trait TestJsonSchemas extends generic.JsonSchemas {
      implicit def localTimeSchema: JsonSchema[LocalDate]

      implicit val timeOrIdSchema: JsonSchema[TimeOrID] = named(genericJsonSchema[TimeOrID], "TimeOrID")
    }

    trait TestEndpoints
        extends algebra.Endpoints
        with algebra.JsonSchemaEntities
        with algebra.JsonSchemas
        with TestJsonSchemas {

      // basic types - build into Elm

      val unitEndpoint: Endpoint[Unit, Unit] =
        endpoint(get(path / "UnitEcho"), emptyResponse(docs = Some("Unit echo")), tags = List("BasicType"))
      val stringEndpoint: Endpoint[(String, String), String] =
        endpoint(
          get(path / "StringEcho" / segment[String]() /? qs[String]("string")),
          jsonResponse[String](docs = Some("String echo")),
          tags = List("BasicType")
        )
      val intEndpoint: Endpoint[(Int, Int), Int] =
        endpoint(
          get(path / "IntEcho" / segment[Int]() /? qs[Int]("int")),
          jsonResponse[Int](docs = Some("Int echo")),
          tags = List("BasicType")
        )
      val longEndpoint: Endpoint[(Long, Long), Long] =
        endpoint(
          get(path / "LongEcho" / segment[Long]() /? qs[Long]("long")),
          jsonResponse[Long](docs = Some("Long echo")),
          tags = List("BasicType")
        )
      val floatEndpoint: Endpoint[Float, Float] =
        endpoint(
          post(path / "FloatEcho", jsonRequest[Float]()),
          jsonResponse[Float](docs = Some("Float echo")),
          tags = List("BasicType")
        )
      val doubleEndpoint: Endpoint[Double, Double] =
        endpoint(
          post(path / "DoubleEcho", jsonRequest[Double]()),
          jsonResponse[Double](docs = Some("Double echo")),
          tags = List("BasicType")
        )
      val booleanEndpoint: Endpoint[Boolean, Boolean] =
        endpoint(
          post(path / "BooleanEcho", jsonRequest[Boolean]()),
          jsonResponse[Boolean](docs = Some("Boolean echo")),
          tags = List("BasicType")
        )
      val uuidEndpoint: Endpoint[(UUID, UUID), UUID] =
        endpoint(
          get(path / "UuidEcho" / segment[UUID]() /? qs[UUID]("uuid")),
          jsonResponse[UUID](docs = Some("UUID echo")),
          tags = List("BasicType")
        )

      // custom basic type - *we* defined it in our application and it can be used by codegen as primitive

      implicit def localTimeSegment: Segment[LocalDate]
      implicit def localTimeQuery: QueryStringParam[LocalDate]
      //implicit def localTimeSchema: JsonSchema[LocalDate]

      val localDateEndpoint: Endpoint[(LocalDate, LocalDate), LocalDate] =
        endpoint(
          get(path / "LocalDateEcho" / segment[LocalDate]() /? qs[LocalDate]("localDate")),
          jsonResponse[LocalDate](docs = Some("LocalDate echo")),
          tags = List("CustomBasicType")
        )

      // product and coproducts

      val timeOrIDEndpoint: Endpoint[TimeOrID, TimeOrID] =
        endpoint(
          post(path / "TimeOrIDEcho", jsonRequest[TimeOrID]()),
          jsonResponse[TimeOrID](docs = Some("TimeOrID echo")),
          tags = List("ProductCoproduct")
        )
    }

    object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator {
      // custom basic type definitions

      implicit def localTimeSegment: ElmType = CustomBasicType("LocalDate")
      implicit def localTimeQuery: ElmType = CustomBasicType("LocalDate")
      implicit def localTimeSchema: ElmType = CustomBasicType("TimeOnly")
    }
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
        uuidEndpoint,
        localDateEndpoint,
        timeOrIDEndpoint
      )() sameAs ReferenceData.from("basic-type-test")(
        "Data/TimeOrID.elm",
        "Data/io.scalaland.endpoints.elm.BasicTypeTest.Domain.TimeOrID.ID.elm",
        "Data/io.scalaland.endpoints.elm.BasicTypeTest.Domain.TimeOrID.Time.elm",
        "Request/BasicType.elm",
        "Request/CustomBasicType.elm",
        "Request/ProductCoproduct.elm"
      )
    }
  }
}
