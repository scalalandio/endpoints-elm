{-
  This file was generated by endpoints-elm 0.9.1 interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Data.Counter exposing (..)


import Json.Decode as Decode exposing (Decoder)
import Json.Decode.Pipeline exposing (optional, required)
import Json.Encode as Encode


type alias Counter =
  { value : Int
  }

init : Counter
init =
  { value = 0
  }

decoder : Decoder Counter
decoder = Decode.succeed Counter
  |> required "value" Decode.int 

encoder : Counter -> Encode.Value
encoder model = Encode.object (fieldsEncoder model)

encoderTagged : (String, String) -> Counter -> Encode.Value
encoderTagged (discriminator, tag) model = Encode.object ((discriminator, Encode.string tag) :: fieldsEncoder model)

fieldsEncoder : Counter -> List (String, Encode.Value)
fieldsEncoder model = 
  [ ( "value", Encode.int model.value )
  ]

setValue : Int -> Counter -> Counter
setValue newValue counter =
  { counter | value = newValue }


updateValue : (Int -> Int) -> Counter -> Counter
updateValue f counter =
  { counter | value = f counter.value }
