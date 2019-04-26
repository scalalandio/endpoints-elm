package io.scalaland.endpoints.elm

import endpoints.algebra

trait Methods extends algebra.Methods {
  type Method = String

  def Get: String = "get"

  def Post: String = "post"

  def Put: String = "put"

  def Delete: String = "delete"

  def Patch: String = "patch"

  def Options: String = "options"
}
