{-
  This file was generated by endpoints-elm 0.10.0 interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.Segments exposing (..)

import Request.Url.Segments
import Http
import HttpBuilder.Task exposing (RequestBuilder)
import Json.Decode as Decode
import Json.Encode as Encode
import Bool.Extra
import Maybe.Extra
import Bytes exposing (Bytes)
import Dict exposing (Dict)

import Date exposing (..)
import Uuid exposing (..)
import EndpointsElm


stringStringGet : String -> RequestBuilder Http.Error ()
stringStringGet string =
  HttpBuilder.Task.get (Request.Url.Segments.stringStringGet string)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


intIntGet : Int -> RequestBuilder Http.Error ()
intIntGet int =
  HttpBuilder.Task.get (Request.Url.Segments.intIntGet int)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


longLongGet : Int -> RequestBuilder Http.Error ()
longLongGet long =
  HttpBuilder.Task.get (Request.Url.Segments.longLongGet long)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


uuidUuidGet : Uuid -> RequestBuilder Http.Error ()
uuidUuidGet uuid =
  HttpBuilder.Task.get (Request.Url.Segments.uuidUuidGet uuid)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


dateDateGet : Date -> RequestBuilder Http.Error ()
dateDateGet date =
  HttpBuilder.Task.get (Request.Url.Segments.dateDateGet date)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000

