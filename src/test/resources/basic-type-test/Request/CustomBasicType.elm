{-
  This file was generated by endpoints-elm interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.CustomBasicType exposing (..)

import Http
import HttpBuilder exposing (RequestBuilder)
import Json.Decode as Decode
import Bool.Extra
import Maybe.Extra




localdateechoGet : LocalDate -> LocalDate -> RequestBuilder TimeOnly
localdateechoGet  local-date =
  HttpBuilder.get ("/local-date-echo/" ++ toString )
    |> HttpBuilder.withQueryParams ([("local-date", toString local-date)])
    |> HttpBuilder.withExpectJson TimeOnly.decoder
    |> HttpBuilder.withTimeout 30000