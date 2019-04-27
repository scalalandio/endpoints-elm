package io.scalaland.endpoints.elm

import endpoints.algebra
import endpoints.algebra.BasicAuthentication.Credentials
import endpoints.algebra.Documentation
import io.scalaland.endpoints.elm.ast.RequiredHeader

trait BasicAuthentication extends algebra.BasicAuthentication with Endpoints {

  def basicAuthenticationHeader: RequestHeaders[Credentials] =
    List(RequiredHeader("Authentication"))

  def authenticated[A](response: Response[A], docs: Documentation = None): Response[Option[A]] =
    response
}
