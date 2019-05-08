package io.scalaland.endpoints.elm

import utest._

object BasicTest extends CodegenTest {

  object Domain {

    import endpoints.{algebra, generic}

    case class Counter(value: Int)

    case class Increment(step: Int)

    trait TestJsonSchemas extends generic.JsonSchemas {

      implicit lazy val counterSchema: JsonSchema[Counter] = named(genericJsonSchema[Counter], "Counter")
      implicit lazy val incrementSchema: JsonSchema[Increment] = named(genericJsonSchema[Increment], "Increment")
    }

    trait TestEndpoints
        extends algebra.Endpoints
        with algebra.JsonSchemaEntities
        with algebra.JsonSchemas
        with TestJsonSchemas {

      val currentValue: Endpoint[Unit, Counter] =
        endpoint(
          get(path / "current-value"),
          jsonResponse[Counter](docs = Some("Coutner status")),
          tags = List("Counter")
        )

      val increment: Endpoint[Increment, Unit] =
        endpoint(
          post(path / "increment", jsonRequest[Increment](docs = Some("Counter increment request"))),
          emptyResponse(),
          tags = List("Counter")
        )
    }

    object TestElmEndpoints extends TestEndpoints with ElmCodeGenerator
  }

  val tests = Tests {
    import Domain.TestElmEndpoints._

    "generate code for simple domain model" - {

      generateElmContents(currentValue, increment)() sameAs ReferenceData.from("basic-test")(
        "Data/Counter.elm",
        "Data/Increment.elm",
        "Request/Counter.elm"
      )
    }
  }
}
