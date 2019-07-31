package io.scalaland.endpoints.elm

import endpoints.algebra
import endpoints.algebra.Documentation
import io.scalaland.endpoints.elm.model._

trait Responses extends algebra.Responses {

  type Response[A] = EncodedType

  def emptyResponse(docs: Documentation): EncodedType =
    NoEntityEncodedType

  def textResponse(docs: Documentation): EncodedType =
    StringEncodedType

  def wheneverFound[A](response: EncodedType,
                       notFoundDocs: Documentation): EncodedType =
    new WrappedEncodedType(response) {
      override def tpe: ElmType = AppliedType.Maybe(response.tpe)
      override def resolveExpr: String = s"EndpointsElm.httpResolveNotFound (${response.resolveExpr})"
    }
}
