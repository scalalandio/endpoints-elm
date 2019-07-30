package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.model.{ElmEndpoint, ElmType}
import utest._

object QueryParamsTest extends CodegenTest {

  import endpoints.algebra

  trait TestEndpoints extends algebra.Endpoints with algebra.JsonSchemaEntities with algebra.JsonSchemas {

    implicit def dateQueryStringParam: QueryStringParam[LocalDate]

    def stringEndpoint: Endpoint[String, Unit] =
      endpoint(get(path / "string" /? qs[String]("string")), emptyResponse(), tags = List("QueryParams"))

    def intEndpoint: Endpoint[Int, Unit] =
      endpoint(get(path / "int" /? qs[Int]("int")), emptyResponse(), tags = List("QueryParams"))

    def longEndpoint: Endpoint[Long, Unit] =
      endpoint(get(path / "long" /? qs[Long]("long")), emptyResponse(), tags = List("QueryParams"))

    def doubleEndpoint: Endpoint[Double, Unit] =
      endpoint(get(path / "double" /? qs[Double]("double")), emptyResponse(), tags = List("QueryParams"))

    def booleanEndpoint: Endpoint[Boolean, Unit] =
      endpoint(get(path / "bool" /? qs[Boolean]("bool")), emptyResponse(), tags = List("QueryParams"))

    def uuidEndpoint: Endpoint[UUID, Unit] =
      endpoint(get(path / "uuid" /? qs[UUID]("uuid")), emptyResponse(), tags = List("QueryParams"))

    def dateEndpoint: Endpoint[LocalDate, Unit] =
      endpoint(get(path / "date" /? qs[LocalDate]("date")), emptyResponse(), tags = List("QueryParams"))

    def listEndpoint: Endpoint[List[Int], Unit] =
      endpoint(get(path / "list" /? qs[List[Int]]("value")), emptyResponse(), tags = List("QueryParams"))

    def optionEndpoint: Endpoint[Option[Int], Unit] =
      endpoint(get(path / "option" /? qs[Option[Int]]("value")), emptyResponse(), tags = List("QueryParams"))

    def manyParamsEndpoint: Endpoint[(Int, Double), Unit] =
      endpoint(get(path / "many-params" /? (qs[Int]("intValue") & qs[Double]("dblValue"))), emptyResponse(), tags = List("QueryParams"))

  }

  object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator {

    implicit def dateQueryStringParam: ElmType = CustomTypes.date

    val allEndpoints: Seq[ElmEndpoint] =
      Seq(stringEndpoint, intEndpoint, longEndpoint, doubleEndpoint, booleanEndpoint, uuidEndpoint, dateEndpoint, listEndpoint, optionEndpoint, manyParamsEndpoint)
  }

  val tests = Tests {
    import TestElmEndpoints._

    "support query parameters" - {

      generateElmContents(allEndpoints: _*)() sameAs ReferenceData.from("query-params-test")("Request/QueryParams.elm")
    }
  }
}
