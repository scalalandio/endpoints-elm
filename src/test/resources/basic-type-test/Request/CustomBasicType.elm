{-
  This file was generated by endpoints-elm interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.CustomBasicType exposing (..)

import Http
import HttpBuilder exposing (RequestBuilder)
import Json.Decode as Decode
import Json.Encode as Encode
import Bool.Extra
import Maybe.Extra

import Date exposing (..)


localdateechoDate1Post : Date -> Date -> Date -> RequestBuilder Date
localdateechoDate1Post date1 date2 date =
  HttpBuilder.post ("/LocalDateEcho/" ++ Date.toIsoString date1)
    |> HttpBuilder.withQueryParams ([("date2", Date.toIsoString date2)])
    |> HttpBuilder.withJsonBody ((Encode.string << Date.toIsoString)  date)
    |> HttpBuilder.withExpectJson (Decode.string |> Decode.andThen (Date.fromIsoString >> Result.map Decode.succeed >> Result.withDefault (Decode.fail "can't parse the date!")))
    |> HttpBuilder.withTimeout 30000
