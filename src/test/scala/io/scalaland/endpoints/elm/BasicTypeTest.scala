package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.model.{CustomBasicType, ElmType}
import utest._

object BasicTypeTest extends CodegenTest {

  object Domain {

    import endpoints.{algebra}
    import io.scalaland.endpoints.macros

    sealed trait TimeOrID
    object TimeOrID {
      final case class Time(value: LocalDate, msg: String) extends TimeOrID
      final case class ID(value: UUID, msg: String) extends TimeOrID
    }

    // we actually need macros instead generic to handle name generation correctly
    trait TestJsonSchemas extends macros.JsonSchemas {
      implicit def localTimeSchema: JsonSchema[LocalDate]

      implicit def timeSchema: JsonSchema[TimeOrID.Time] = named(genericJsonSchema[TimeOrID.Time], "Time")
      implicit def idSchema: JsonSchema[TimeOrID.ID] = named(genericJsonSchema[TimeOrID.ID], "ID")
      implicit def timeOrIdSchema: JsonSchema[TimeOrID] = named(genericJsonSchema[TimeOrID], "TimeOrID")
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
          get(path / "StringEcho" / segment[String]("string1") /? qs[String]("string2")),
          jsonResponse[String](docs = Some("String echo")),
          tags = List("BasicType")
        )
      val intEndpoint: Endpoint[(Int, Int), Int] =
        endpoint(
          get(path / "IntEcho" / segment[Int]("int1") /? qs[Int]("int2")),
          jsonResponse[Int](docs = Some("Int echo")),
          tags = List("BasicType")
        )
      val longEndpoint: Endpoint[(Long, Long), Long] =
        endpoint(
          get(path / "LongEcho" / segment[Long]("long1") /? qs[Long]("long2")),
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
          get(path / "UuidEcho" / segment[UUID]("uuid1") /? qs[UUID]("uuid2")),
          jsonResponse[UUID](docs = Some("UUID echo")),
          tags = List("BasicType")
        )

      // custom basic type - *we* defined it in our application and it can be used by codegen as primitive

      implicit def localTimeSegment: Segment[LocalDate]
      implicit def localTimeQuery: QueryStringParam[LocalDate]

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
        "Data/ID.elm",
        "Data/Time.elm",
        "Request/BasicType.elm",
        "Request/CustomBasicType.elm",
        "Request/ProductCoproduct.elm"
      )
    }
  }
}
