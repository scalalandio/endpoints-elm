package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.fixtures._
import io.scalaland.endpoints.elm.model.{ElmEndpoint, ElmType}
import utest._

object RequestsTest extends CodegenTest {

  import endpoints.algebra

  trait TestEndpoints extends algebra.Endpoints with algebra.JsonSchemaEntities with fixtures.JsonSchemas {

    def textEndpoint: Endpoint[String, Unit] =
      endpoint(post(path / "text", textRequest()), emptyResponse(), tags = List("Requests"))

    def stringEndpoint: Endpoint[String, Unit] =
      endpoint(post(path / "json-string", jsonRequest[String]()), emptyResponse(), tags = List("Requests"))

    def intEndpoint: Endpoint[Int, Unit] =
      endpoint(post(path / "json-int", jsonRequest[Int]()), emptyResponse(), tags = List("Requests"))

    def longEndpoint: Endpoint[Long, Unit] =
      endpoint(post(path / "json-long", jsonRequest[Long]()), emptyResponse(), tags = List("Requests"))

    def floatEndpoint: Endpoint[Float, Unit] =
      endpoint(post(path / "json-float", jsonRequest[Float]()), emptyResponse(), tags = List("Requests"))

    def doubleEndpoint: Endpoint[Double, Unit] =
      endpoint(post(path / "json-double", jsonRequest[Double]()), emptyResponse(), tags = List("Requests"))

    def booleanEndpoint: Endpoint[Boolean, Unit] =
      endpoint(post(path / "json-boolean", jsonRequest[Boolean]()), emptyResponse(), tags = List("Requests"))

    def uuidEndpoint: Endpoint[UUID, Unit] =
      endpoint(post(path / "json-uuid", jsonRequest[UUID]()), emptyResponse(), tags = List("Requests"))

    def dateEndpoint: Endpoint[LocalDate, Unit] =
      endpoint(post(path / "json-date", jsonRequest[LocalDate]()), emptyResponse(), tags = List("Requests"))

    def caseClassEndpoint: Endpoint[Foo, Unit] =
      endpoint(post(path / "json-case-class", jsonRequest[Foo]()), emptyResponse(), tags = List("Requests"))

    def coproductEndpoint: Endpoint[Coproduct, Unit] =
      endpoint(post(path / "json-coproduct", jsonRequest[Coproduct]()), emptyResponse(), tags = List("Requests"))

    def listEndpoint: Endpoint[List[String], Unit] =
      endpoint(post(path / "json-list", jsonRequest[List[String]]()), emptyResponse(), tags = List("Requests"))

    def mapEndpoint: Endpoint[Map[String, Foo], Unit] =
      endpoint(post(path / "json-map", jsonRequest[Map[String, Foo]]()), emptyResponse(), tags = List("Requests"))
  }

  object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator {

    implicit def dateSchema: ElmType = CustomTypes.date

    val allEndpoints: Seq[ElmEndpoint] = Seq(
      stringEndpoint,
      intEndpoint,
      longEndpoint,
      floatEndpoint,
      doubleEndpoint,
      booleanEndpoint,
      uuidEndpoint,
      dateEndpoint,
      caseClassEndpoint,
      coproductEndpoint,
      listEndpoint,
      mapEndpoint
    )
  }

  val tests = Tests {
    import TestElmEndpoints._

    "support requests" - {

      //      ReferenceData.save("/Users/krzemin/Projects/Scalaland/endpoints-elm/src/test/resources/requests-test")(
      //        generateElmContents(allEndpoints: _*)()
      //      )

      generateElmContents(allEndpoints: _*)() sameAs ReferenceData.from("requests-test")(
        "Data/Coproduct.elm",
        "Data/Foo.elm",
        "Data/Inst1.elm",
        "Data/Inst2.elm",
        "Request/Requests.elm"
      )
    }
  }
}
