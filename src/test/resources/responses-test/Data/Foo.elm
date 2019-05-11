{-
  This file was generated by endpoints-elm 0.9.1 interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Data.Foo exposing (..)

import Data.Inst1 exposing (..)
import Data.Inst2 exposing (..)
import Json.Decode as Decode exposing (Decoder)
import Json.Decode.Pipeline exposing (optional, required)
import Json.Encode as Encode


type alias Foo =
  { foo1 : Int
  , foo2 : Int
  , foo3 : Inst1
  , foo4 : Maybe Inst2
  }

init : Foo
init =
  { foo1 = 0
  , foo2 = 0
  , foo3 = Data.Inst1.init
  , foo4 = Nothing
  }

decoder : Decoder Foo
decoder = Decode.succeed Foo
  |> required "foo1" Decode.int 
  |> required "foo2" Decode.int 
  |> required "foo3" Data.Inst1.decoder 
  |> optional "foo4" (Decode.nullable Data.Inst2.decoder) Nothing

encoder : Foo -> Encode.Value
encoder model = Encode.object (fieldsEncoder model)

encoderTagged : (String, String) -> Foo -> Encode.Value
encoderTagged (discriminator, tag) model = Encode.object ((discriminator, Encode.string tag) :: fieldsEncoder model)

fieldsEncoder : Foo -> List (String, Encode.Value)
fieldsEncoder model = 
  [ ( "foo1", Encode.int model.foo1 )
  , ( "foo2", Encode.int model.foo2 )
  , ( "foo3", Data.Inst1.encoder model.foo3 )
  , ( "foo4", Maybe.withDefault Encode.null (Maybe.map Data.Inst2.encoder  model.foo4) )
  ]

setFoo1 : Int -> Foo -> Foo
setFoo1 newFoo1 foo =
  { foo | foo1 = newFoo1 }

setFoo2 : Int -> Foo -> Foo
setFoo2 newFoo2 foo =
  { foo | foo2 = newFoo2 }

setFoo3 : Inst1 -> Foo -> Foo
setFoo3 newFoo3 foo =
  { foo | foo3 = newFoo3 }

setFoo4 : Maybe Inst2 -> Foo -> Foo
setFoo4 newFoo4 foo =
  { foo | foo4 = newFoo4 }


updateFoo1 : (Int -> Int) -> Foo -> Foo
updateFoo1 f foo =
  { foo | foo1 = f foo.foo1 }

updateFoo2 : (Int -> Int) -> Foo -> Foo
updateFoo2 f foo =
  { foo | foo2 = f foo.foo2 }

updateFoo3 : (Inst1 -> Inst1) -> Foo -> Foo
updateFoo3 f foo =
  { foo | foo3 = f foo.foo3 }

updateFoo4 : (Maybe Inst2 -> Maybe Inst2) -> Foo -> Foo
updateFoo4 f foo =
  { foo | foo4 = f foo.foo4 }
