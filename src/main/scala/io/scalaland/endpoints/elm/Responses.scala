package io.scalaland.endpoints.elm

import endpoints.algebra
import endpoints.algebra.Documentation
import io.scalaland.endpoints.elm.ast._

trait Responses extends algebra.Responses {

  type Response[A] = (ElmEntityEncoding, ElmType)

  def emptyResponse(docs: Documentation): (ElmEntityEncoding, ElmType) =
    (NoEntity, BasicType.Unit)

  def textResponse(docs: Documentation): (ElmEntityEncoding, ElmType) =
    (StringEncoding, BasicType.String)

  def wheneverFound[A](response: (ElmEntityEncoding, ElmType),
                       notFoundDocs: Documentation): (ElmEntityEncoding, ElmType) =
    response
}
