package io.scalaland.endpoints.elm

import endpoints.algebra
import endpoints.algebra.Documentation
import io.scalaland.endpoints.elm.model._

trait JsonSchemaEntities extends algebra.JsonSchemaEntities with Endpoints with JsonSchemas {

  def jsonRequest[A](docs: Documentation = None)(implicit tpe: JsonRequest[A]): RequestEntity[A] =
    (JsonEncoding, tpe)

  def jsonResponse[A](docs: Documentation = None)(implicit tpe: JsonResponse[A]): Response[A] =
    (JsonEncoding, tpe)
}
