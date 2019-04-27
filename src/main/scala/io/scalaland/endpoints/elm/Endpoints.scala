package io.scalaland.endpoints.elm

import endpoints.algebra
import endpoints.algebra.Documentation
import io.scalaland.endpoints.elm.model._

trait Endpoints extends algebra.Endpoints with Requests with Responses {

  type Endpoint[A, B] = ElmEndpoint

  def endpoint[A, B](request: Request[A],
                     response: Response[B],
                     summary: Documentation = None,
                     description: Documentation = None,
                     tags: List[String] = Nil): Endpoint[A, B] =
    ElmEndpoint(request.name, request, response._1, response._2, summary, description, tags)
}
