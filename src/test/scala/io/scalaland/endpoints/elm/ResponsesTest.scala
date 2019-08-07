package io.scalaland.endpoints.elm

import java.time.LocalDate
import java.util.UUID

import io.scalaland.endpoints.elm.fixtures.{Coproduct, Foo}
import io.scalaland.endpoints.elm.model.{ElmEndpoint, ElmType}
import io.scalaland.endpoints.elm.commons.{CodegenTest, CustomTypes, ReferenceData, UnitStatusCodes}
import utest._

object ResponsesTest extends CodegenTest {

  import endpoints.algebra

  trait TestEndpoints extends algebra.Endpoints with algebra.JsonSchemaEntities with fixtures.JsonSchemas {

    def textEndpoint: Endpoint[Unit, String] =
      endpoint(get(path / "text"), textResponse(), tags = List("Responses"))

    def stringEndpoint: Endpoint[Unit, String] =
      endpoint(get(path / "json-string"), jsonResponse[String](), tags = List("Responses"))

    def intEndpoint: Endpoint[Unit, Int] =
      endpoint(get(path / "json-int"), jsonResponse[Int](), tags = List("Responses"))

    def longEndpoint: Endpoint[Unit, Long] =
      endpoint(get(path / "json-long"), jsonResponse[Long](), tags = List("Responses"))

    def floatEndpoint: Endpoint[Unit, Float] =
      endpoint(get(path / "json-float"), jsonResponse[Float](), tags = List("Responses"))

    def doubleEndpoint: Endpoint[Unit, Double] =
      endpoint(get(path / "json-double"), jsonResponse[Double](), tags = List("Responses"))

    def booleanEndpoint: Endpoint[Unit, Boolean] =
      endpoint(get(path / "json-boolean"), jsonResponse[Boolean](), tags = List("Responses"))

    def uuidEndpoint: Endpoint[Unit, UUID] =
      endpoint(get(path / "json-uuid"), jsonResponse[UUID](), tags = List("Responses"))

    def dateEndpoint: Endpoint[Unit, LocalDate] =
      endpoint(get(path / "json-date"), jsonResponse[LocalDate](), tags = List("Responses"))

    def caseClassEndpoint: Endpoint[Unit, Foo] =
      endpoint(get(path / "json-case-class"), jsonResponse[Foo](), tags = List("Responses"))

    def coproductEndpoint: Endpoint[Unit, Coproduct] =
      endpoint(get(path / "json-coproduct"), jsonResponse[Coproduct](), tags = List("Responses"))

    def listEndpoint: Endpoint[Unit, List[String]] =
      endpoint(get(path / "json-list"), jsonResponse[List[String]](), tags = List("Responses"))

    def mapEndpoint: Endpoint[Unit, Map[String, Foo]] =
      endpoint(get(path / "json-map"), jsonResponse[Map[String, Foo]](), tags = List("Responses"))
  }

  object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator with UnitStatusCodes {

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

    "support Responses" - {

//      writeElmCode("src/test/resources/responses-test")(allEndpoints: _*)()

      generateElmContents(allEndpoints: _*)() sameAs ReferenceData.from("responses-test")(
        "EndpointsElm.elm",
        "Data/Coproduct.elm",
        "Data/Foo.elm",
        "Data/Inst1.elm",
        "Data/Inst2.elm",
        "Request/Url/Responses.elm",
        "Request/Responses.elm"
      )
    }
  }
}
