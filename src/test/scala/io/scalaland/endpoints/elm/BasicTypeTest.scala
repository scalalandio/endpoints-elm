package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.model.{CustomBasicType, ElmEndpoint, ElmType}
import utest._

object BasicTypeTest extends CodegenTest {

  object Domain {

    import endpoints.algebra
    import io.scalaland.endpoints.macros

    sealed trait DateOrUuid
    object DateOrUuid {
      final case class DateCase(value: LocalDate, msg: String) extends DateOrUuid
      final case class UuidCase(value: UUID, msg: String) extends DateOrUuid
    }

    // we actually need macros instead generic to handle name generation correctly
    trait TestJsonSchemas extends macros.JsonSchemas {
      implicit def dateSchema: JsonSchema[LocalDate]

      implicit def dateCaseSchema: JsonSchema[DateOrUuid.DateCase] =
        named(genericJsonSchema[DateOrUuid.DateCase], "DateCase")
      implicit def uuidCaseSchema: JsonSchema[DateOrUuid.UuidCase] =
        named(genericJsonSchema[DateOrUuid.UuidCase], "UuidCase")
      implicit def dateOrUuidSchema: JsonSchema[DateOrUuid] = named(genericJsonSchema[DateOrUuid], "DateOrUuid")
    }

    trait TestEndpoints
        extends algebra.Endpoints
        with algebra.JsonSchemaEntities
        with algebra.JsonSchemas
        with TestJsonSchemas {

      // basic types - build into Elm

      def unitEndpoint: Endpoint[Unit, Unit] =
        endpoint(get(path / "UnitEcho"), emptyResponse(docs = Some("Unit echo")), tags = List("BasicType"))

      def stringEndpoint: Endpoint[(String, String), String] =
        endpoint(
          get(path / "StringEcho" / segment[String]("string1") /? qs[String]("string2")),
          jsonResponse[String](docs = Some("String echo")),
          tags = List("BasicType")
        )

      def intEndpoint: Endpoint[(Int, Int), Int] =
        endpoint(
          get(path / "IntEcho" / segment[Int]("int1") /? qs[Int]("int2")),
          jsonResponse[Int](docs = Some("Int echo")),
          tags = List("BasicType")
        )

      def longEndpoint: Endpoint[(Long, Long), Long] =
        endpoint(
          get(path / "LongEcho" / segment[Long]("long1") /? qs[Long]("long2")),
          jsonResponse[Long](docs = Some("Long echo")),
          tags = List("BasicType")
        )

      def floatEndpoint: Endpoint[Float, Float] =
        endpoint(
          post(path / "FloatEcho", jsonRequest[Float]()),
          jsonResponse[Float](docs = Some("Float echo")),
          tags = List("BasicType")
        )

      def doubleEndpoint: Endpoint[Double, Double] =
        endpoint(
          post(path / "DoubleEcho", jsonRequest[Double]()),
          jsonResponse[Double](docs = Some("Double echo")),
          tags = List("BasicType")
        )

      def booleanEndpoint: Endpoint[Boolean, Boolean] =
        endpoint(
          post(path / "BooleanEcho", jsonRequest[Boolean]()),
          jsonResponse[Boolean](docs = Some("Boolean echo")),
          tags = List("BasicType")
        )

      def uuidEndpoint: Endpoint[(UUID, UUID), UUID] =
        endpoint(
          get(path / "UuidEcho" / segment[UUID]("uuid1") /? qs[UUID]("uuid2")),
          jsonResponse[UUID](docs = Some("UUID echo")),
          tags = List("BasicType")
        )

      // custom basic type - *we* defined it in our application and it can be used by codegen as primitive

      implicit def dateSegment: Segment[LocalDate]
      implicit def dateQueryStringParam: QueryStringParam[LocalDate]

      def localDateEndpoint: Endpoint[(LocalDate, LocalDate), LocalDate] =
        endpoint(
          get(path / "LocalDateEcho" / segment[LocalDate]("date1") /? qs[LocalDate]("date2")),
          jsonResponse[LocalDate](docs = Some("LocalDate echo")),
          tags = List("CustomBasicType")
        )

      // product and coproducts

      def dateOrIDEndpoint: Endpoint[DateOrUuid, DateOrUuid] =
        endpoint(
          post(path / "DateOrIDEcho", jsonRequest[DateOrUuid]()),
          jsonResponse[DateOrUuid](docs = Some("DateOrID echo")),
          tags = List("ProductCoproduct")
        )
    }

    object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator {
      // custom basic type definitions

      val dateCustomType = CustomBasicType(
        "Date",
        "Date.fromOrdinalDate 1970 1",
        "Encode.string << Date.toIsoString",
        """Decode.string |> Decode.andThen (Date.fromIsoString >> Result.map Decode.succeed >> Result.withDefault (Decode.fail "can't parse the date!"))""",
        "Date.toIsoString"
      )

      implicit def dateSegment: ElmType = dateCustomType
      implicit def dateQueryStringParam: ElmType = dateCustomType
      implicit def dateSchema: ElmType = dateCustomType

      val allEndpoints: Seq[ElmEndpoint] = Seq(
        unitEndpoint,
        stringEndpoint,
        intEndpoint,
        longEndpoint,
        floatEndpoint,
        doubleEndpoint,
        booleanEndpoint,
        uuidEndpoint,
        localDateEndpoint,
        dateOrIDEndpoint
      )
    }
  }

  val tests = Tests {
    import Domain.TestElmEndpoints._

    "generate code for simple domain model" - {

      generateElmContents(allEndpoints: _*)() sameAs ReferenceData.from("basic-type-test")(
        "Data/DateOrUuid.elm",
        "Data/DateCase.elm",
        "Data/UuidCase.elm",
        "Request/BasicType.elm",
        "Request/CustomBasicType.elm",
        "Request/ProductCoproduct.elm"
      )
    }
  }
}
