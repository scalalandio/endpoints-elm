package io.scalaland.endpoints.elm

import endpoints.algebra
import endpoints.algebra.Documentation
import io.scalaland.endpoints.elm.model._

trait Responses extends algebra.Responses {

  type Response[A] = (ElmEntityEncoding, ElmType)

  def emptyResponse(docs: Documentation): (ElmEntityEncoding, ElmType) =
    (NoEntity, BasicType.Unit)

  def textResponse(docs: Documentation): (ElmEntityEncoding, ElmType) =
    (StringEncoding, BasicType.String)

  // TODO: try to model with encoded type so that return type is lifted, as in Scala client interpreters
  def wheneverFound[A](response: (ElmEntityEncoding, ElmType),
                       notFoundDocs: Documentation): (ElmEntityEncoding, ElmType) =
    response
}
