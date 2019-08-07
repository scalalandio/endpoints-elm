package io.scalaland.endpoints.elm

import io.scalaland.endpoints.elm.model.ElmEndpoint
import io.scalaland.endpoints.elm.commons.{CodegenTest, ReferenceData, UnitStatusCodes}
import utest._

object CounterTest extends CodegenTest {

  import endpoints.algebra
  import io.scalaland.endpoints.macros

  case class Counter(value: Int)

  case class Increment(step: Int)

  trait TestEndpoints
      extends algebra.Endpoints
      with algebra.JsonSchemaEntities
      with algebra.JsonSchemas
      with macros.JsonSchemas {

    implicit lazy val counterSchema: JsonSchema[Counter] = named(genericJsonSchema[Counter], "Counter")
    implicit lazy val incrementSchema: JsonSchema[Increment] = named(genericJsonSchema[Increment], "Increment")

    val currentValue: Endpoint[Unit, Counter] =
      endpoint(
        get(path / "current-value"),
        jsonResponse[Counter](docs = Some("Counter status")),
        tags = List("Counter")
      )

    val increment: Endpoint[Increment, Unit] =
      endpoint(
        post(path / "increment", jsonRequest[Increment](docs = Some("Counter increment request"))),
        emptyResponse(),
        tags = List("Counter")
      )
  }

  object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator with UnitStatusCodes {

    val allEndpoints: Seq[ElmEndpoint] =
      Seq(currentValue, increment)
  }

  val tests = Tests {
    import TestElmEndpoints._

    "generate code for simple domain model" - {

//      writeElmCode("src/test/resources/counter-test")(allEndpoints: _*)()

      generateElmContents(allEndpoints: _*)() sameAs ReferenceData.from("counter-test")(
        "EndpointsElm.elm",
        "Data/Counter.elm",
        "Data/Increment.elm",
        "Request/Url/Counter.elm",
        "Request/Counter.elm"
      )
    }
  }
}
