package io.scalaland.endpoints.elm

import endpoints.algebra

trait StatusCodes extends algebra.StatusCodes {

  type StatusCode = Unit

  def OK: Unit = ()
  def BadRequest: Unit = ()
  def Unauthorized: Unit = ()
  def NotFound: Unit = ()
}
