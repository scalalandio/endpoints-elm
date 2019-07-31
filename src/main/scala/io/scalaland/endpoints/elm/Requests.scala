package io.scalaland.endpoints.elm

import endpoints.algebra.Documentation
import endpoints.{InvariantFunctor, Semigroupal, Tupler, algebra}
import io.scalaland.endpoints.elm.model._

trait Requests extends algebra.Requests with Urls with Methods {

  type RequestHeaders[A] = List[ElmHeader]

  def emptyHeaders: RequestHeaders[Unit] = Nil

  def header(name: String, docs: Documentation = None): RequestHeaders[String] =
    List(RequiredHeader(name))

  def optHeader(name: String, docs: Documentation = None): RequestHeaders[Option[String]] =
    List(OptionalHeader(name))

  implicit def reqHeadersSemigroupal: Semigroupal[RequestHeaders] = new Semigroupal[RequestHeaders] {
    def product[A, B](fa: RequestHeaders[A], fb: RequestHeaders[B])(
      implicit tupler: Tupler[A, B]
    ): RequestHeaders[tupler.Out] = fa ++ fb
  }

  implicit def reqHeadersInvFunctor: InvariantFunctor[RequestHeaders] = new InvariantFunctor[RequestHeaders] {
    def xmap[From, To](f: List[ElmHeader], map: From => To, contramap: To => From): List[ElmHeader] = f
  }

  type RequestEntity[A] = EncodedType

  implicit def reqEntityInvFunctor: InvariantFunctor[RequestEntity] = new InvariantFunctor[RequestEntity] {
    def xmap[From, To](f: EncodedType,
                       map: From => To,
                       contramap: To => From): EncodedType = f
  }

  def emptyRequest: RequestEntity[Unit] = NoEntityEncodedType

  def textRequest(docs: Documentation = None): RequestEntity[String] = StringEncodedType

  type Request[A] = ElmRequest

  def request[UrlP, BodyP, HeadersP, UrlAndBodyPTupled, Out](
    method: Method,
    url: Url[UrlP],
    entity: RequestEntity[BodyP] = emptyRequest,
    headers: RequestHeaders[HeadersP] = emptyHeaders
  )(implicit tuplerUB: Tupler.Aux[UrlP, BodyP, UrlAndBodyPTupled],
    tuplerUBH: Tupler.Aux[UrlAndBodyPTupled, HeadersP, Out]): Request[Out] =
    ElmRequest(method, url, entity, headers)
}
