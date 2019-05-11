{-
  This file was generated by endpoints-elm interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Data.Increment exposing (..)


import Json.Decode as Decode exposing (Decoder)
import Json.Decode.Pipeline exposing (optional, required)
import Json.Encode as Encode


type alias Increment =
  { step : Int
  }

init : Increment
init =
  { step = 0
  }

decoder : Decoder Increment
decoder = Decode.succeed Increment
  |> required "step" Decode.int 

encoder : Increment -> Encode.Value
encoder model = Encode.object (fieldsEncoder model)

encoderTagged : (String, String) -> Increment -> Encode.Value
encoderTagged (discriminator, tag) model = Encode.object ((discriminator, Encode.string tag) :: fieldsEncoder model)

fieldsEncoder : Increment -> List (String, Encode.Value)
fieldsEncoder model = 
  [ ( "step", Encode.int model.step )
  ]

setStep : Int -> Increment -> Increment
setStep newStep increment =
  { increment | step = newStep }


updateStep : (Int -> Int) -> Increment -> Increment
updateStep f increment =
  { increment | step = f increment.step }
