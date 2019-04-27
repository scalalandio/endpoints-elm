package io.scalaland.endpoints.elm

import endpoints.generic

case class Counter(value: Int)
case class Increment(step: Int)

trait TestJsonSchemas extends generic.JsonSchemas {

  lazy val counterSchema: JsonSchema[Counter] = named(genericJsonSchema[Counter], "Counter")
  lazy val incrementSchema: JsonSchema[Increment] = named(genericJsonSchema[Increment], "Increment")
}


object TestEndpoints extends ElmCodeGenerator with TestJsonSchemas {

  val currentValue: Endpoint[Unit, Counter] =
    endpoint(
      get(path / "current-value"),
      jsonResponse[Counter](docs = Some("Coutner status"))(counterSchema),
      tags = List("Counter")
    )

  val increment: Endpoint[Increment, Unit] =
    endpoint(
      post(path / "increment", jsonRequest[Increment](docs = Some("Counter increment request"))(incrementSchema)),
      emptyResponse(),
      tags = List("Counter")
    )
}
