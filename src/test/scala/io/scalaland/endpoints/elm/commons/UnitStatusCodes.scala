package io.scalaland.endpoints.elm.commons

import endpoints.algebra

trait UnitStatusCodes extends algebra.StatusCodes {

  type StatusCode = Unit

  def OK: Unit = ()
  def BadRequest: Unit = ()
  def Unauthorized: Unit = ()
  def NotFound: Unit = ()
}
