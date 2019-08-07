package io.scalaland.endpoints.elm

import endpoints.algebra.Documentation
import endpoints.{InvariantFunctor, PartialInvariantFunctor, Tupler, algebra}
import io.scalaland.endpoints.elm.model._

import scala.collection.compat.Factory
import language.higherKinds

trait Urls extends algebra.Urls {

  type QueryString[A] = List[(String, ElmType)]

  def combineQueryStrings[A, B](first: QueryString[A],
                                second: QueryString[B])(implicit tupler: Tupler[A, B]): QueryString[tupler.Out] =
    first ++ second

  def qs[A](name: String, docs: Documentation = None)(implicit tpe: QueryStringParam[A]): QueryString[A] =
    List(name -> tpe)

  implicit def optionalQueryStringParam[A](implicit tpe: QueryStringParam[A]): QueryStringParam[Option[A]] =
    AppliedType.Maybe(tpe)

  implicit def repeatedQueryStringParam[A, CC[X] <: Iterable[X]](implicit tpe: QueryStringParam[A],
                                                                 factory: Factory[A, CC[A]]): QueryStringParam[CC[A]] =
    AppliedType.List(tpe)

  type QueryStringParam[A] = ElmType

  override implicit def uuidQueryString: QueryStringParam[String] = BasicType.Uuid

  implicit def stringQueryString: QueryStringParam[String] = BasicType.String

  override implicit def intQueryString: QueryStringParam[Int] = BasicType.Int

  override implicit def longQueryString: QueryStringParam[Long] = BasicType.Int

  override implicit def booleanQueryString: QueryStringParam[Boolean] = BasicType.Bool

  override implicit def doubleQueryString: QueryStringParam[Double] = BasicType.Float

  type Segment[A] = ElmType

  override implicit def uuidSegment: Segment[String] = BasicType.Uuid

  implicit def stringSegment: Segment[String] = BasicType.String

  override implicit def intSegment: Segment[Int] = BasicType.Int

  override implicit def longSegment: Segment[Long] = BasicType.Int

  type Path[A] = ElmUrl
  type Url[A] = ElmUrl

  def staticPathSegment(segment: String): Path[Unit] =
    ElmUrl(List(StaticSegment(segment)), Nil)

  def segment[A](name: String = "", docs: Documentation = None)(implicit s: Segment[A]): Path[A] =
    ElmUrl(List(VariableSegment(name, s)), Nil)

  def chainPaths[A, B](first: Path[A], second: Path[B])(implicit tupler: Tupler[A, B]): Path[tupler.Out] =
    ElmUrl(segments = first.segments ++ second.segments, queryParams = first.queryParams ++ second.queryParams)

  implicit def urlInvFunctor: InvariantFunctor[Url] = new InvariantFunctor[Url] {
    def xmap[From, To](f: Url[From], map: From => To, contramap: To => From): Url[To] = f
  }

  def urlWithQueryString[A, B](path: Path[A], qs: QueryString[B])(implicit tupler: Tupler[A, B]): Url[tupler.Out] =
    path.copy(queryParams = qs)

  implicit def queryStringPartialInvFunctor: PartialInvariantFunctor[QueryString] = null

  implicit def queryStringParamPartialInvFunctor: PartialInvariantFunctor[QueryStringParam] = null

  implicit def segmentPartialInvFunctor: PartialInvariantFunctor[Segment] = null

  implicit def pathPartialInvariantFunctor: PartialInvariantFunctor[Path] = null

  def remainingSegments(name: String, docs: Documentation): ElmUrl = null

  implicit def urlPartialInvFunctor: PartialInvariantFunctor[Url] = null
}
