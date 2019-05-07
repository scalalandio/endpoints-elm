{-
  This file was generated by endpoints-elm interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.BasicType exposing (..)

import Http
import HttpBuilder exposing (RequestBuilder)
import Json.Decode as Decode
import Bool.Extra
import Maybe.Extra

import Uuid exposing (..)


unitechoGet : RequestBuilder ()
unitechoGet  =
  HttpBuilder.get "/UnitEcho"
    |> HttpBuilder.withExpect (Http.expectStringResponse (\_ -> Ok ()))
    |> HttpBuilder.withTimeout 30000


stringechoGet : String -> String -> RequestBuilder String
stringechoGet  string =
  HttpBuilder.get ("/StringEcho/" ++ )
    |> HttpBuilder.withQueryParams ([("string",  string)])
    |> HttpBuilder.withExpectJson Decode.string
    |> HttpBuilder.withTimeout 30000


intechoGet : Int -> Int -> RequestBuilder Int
intechoGet  int =
  HttpBuilder.get ("/IntEcho/" ++ String.fromInt )
    |> HttpBuilder.withQueryParams ([("int", String.fromInt int)])
    |> HttpBuilder.withExpectJson Decode.int
    |> HttpBuilder.withTimeout 30000


longechoGet : Int -> Int -> RequestBuilder Int
longechoGet  long =
  HttpBuilder.get ("/LongEcho/" ++ String.fromInt )
    |> HttpBuilder.withQueryParams ([("long", String.fromInt long)])
    |> HttpBuilder.withExpectJson Decode.int
    |> HttpBuilder.withTimeout 30000


floatechoPost : Float -> RequestBuilder Float
floatechoPost float =
  HttpBuilder.post "/FloatEcho"
    |> HttpBuilder.withJsonBody (Encode.float  float)
    |> HttpBuilder.withExpectJson Decode.float
    |> HttpBuilder.withTimeout 30000


doubleechoPost : Float -> RequestBuilder Float
doubleechoPost float =
  HttpBuilder.post "/DoubleEcho"
    |> HttpBuilder.withJsonBody (Encode.float  float)
    |> HttpBuilder.withExpectJson Decode.float
    |> HttpBuilder.withTimeout 30000


booleanechoPost : Bool -> RequestBuilder Bool
booleanechoPost bool =
  HttpBuilder.post "/BooleanEcho"
    |> HttpBuilder.withJsonBody (Encode.bool  bool)
    |> HttpBuilder.withExpectJson Decode.bool
    |> HttpBuilder.withTimeout 30000


uuidechoGet : Uuid -> Uuid -> RequestBuilder Uuid
uuidechoGet  uuid =
  HttpBuilder.get ("/UuidEcho/" ++ Uuid.toString )
    |> HttpBuilder.withQueryParams ([("uuid", Uuid.toString uuid)])
    |> HttpBuilder.withExpectJson Uuid.decoder
    |> HttpBuilder.withTimeout 30000
