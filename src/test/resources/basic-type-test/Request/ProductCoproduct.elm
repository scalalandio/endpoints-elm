{-
  This file was generated by endpoints-elm interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.ProductCoproduct exposing (..)

import Http
import HttpBuilder exposing (RequestBuilder)
import Json.Decode as Decode
import Bool.Extra
import Maybe.Extra

import Data.TimeOrID exposing (..)


timeoridechoPost : TimeOrID -> RequestBuilder TimeOrID
timeoridechoPost timeOrID =
  HttpBuilder.post "/time-or-id-echo"
    |> HttpBuilder.withJsonBody (Data.TimeOrID.encoder  timeOrID)
    |> HttpBuilder.withExpectJson Data.TimeOrID.decoder
    |> HttpBuilder.withTimeout 30000
