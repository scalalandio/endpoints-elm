{-
  This file was generated by endpoints-elm 0.9.2 interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.QueryParams exposing (..)

import Request.Url.QueryParams
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


stringGet : String -> RequestBuilder Http.Error ()
stringGet string =
  HttpBuilder.Task.get (Request.Url.QueryParams.stringGet string)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


intGet : Int -> RequestBuilder Http.Error ()
intGet int =
  HttpBuilder.Task.get (Request.Url.QueryParams.intGet int)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


longGet : Int -> RequestBuilder Http.Error ()
longGet long =
  HttpBuilder.Task.get (Request.Url.QueryParams.longGet long)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


doubleGet : Float -> RequestBuilder Http.Error ()
doubleGet double =
  HttpBuilder.Task.get (Request.Url.QueryParams.doubleGet double)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


boolGet : Bool -> RequestBuilder Http.Error ()
boolGet bool =
  HttpBuilder.Task.get (Request.Url.QueryParams.boolGet bool)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


uuidGet : Uuid -> RequestBuilder Http.Error ()
uuidGet uuid =
  HttpBuilder.Task.get (Request.Url.QueryParams.uuidGet uuid)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


dateGet : Date -> RequestBuilder Http.Error ()
dateGet date =
  HttpBuilder.Task.get (Request.Url.QueryParams.dateGet date)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


listGet : (List Int) -> RequestBuilder Http.Error ()
listGet value =
  HttpBuilder.Task.get (Request.Url.QueryParams.listGet value)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


optionGet : (Maybe Int) -> RequestBuilder Http.Error ()
optionGet value =
  HttpBuilder.Task.get (Request.Url.QueryParams.optionGet value)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000


manyparamsGet : Int -> Float -> RequestBuilder Http.Error ()
manyparamsGet intValue dblValue =
  HttpBuilder.Task.get (Request.Url.QueryParams.manyparamsGet intValue dblValue)
    |> HttpBuilder.Task.withResolver (Http.stringResolver (EndpointsElm.httpResolveUnit))
    |> HttpBuilder.Task.withTimeout 30000

