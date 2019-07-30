# endpoints-elm

[![Build Status](https://travis-ci.org/scalalandio/endpoints-elm.svg?branch=master)](https://travis-ci.org/scalalandio/endpoints-elm)
[![Maven Central](https://img.shields.io/maven-central/v/io.scalaland/endpoints-elm_2.12.svg)](http://search.maven.org/#search%7Cga%7C1%7Cendpoints-elm)
[![License](http://img.shields.io/:license-Apache%202-green.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

Elm code generator based on Scala endpoints library (https://github.com/julienrf/endpoints).

#### New to endpoints?

Endpoints is great Scala library that allows you to define communication protocols over HTTP and keep
consistent server implementation, clients and documentation.
See also [official project documentation](http://julienrf.github.io/endpoints/)

#### Elm code generator

This project provides:

- interpreter for all basic endpoints algebras that targets [data structures](src/main/scala/io/scalaland/endpoints/elm/model) that resemble elm type system
- code emitter that takes these data structures and emits elm code containing:
  - elm type definitions
  - json encoders and decoders
  - init value providers
  - api client methods targeting [elm-http-builder](https://github.com/lukewestby/elm-http-builder)

##### Output code structure

```
Data/Type1.elm
Data/Type2.elm
Data/Type3.elm
Request/HttpModule1.elm
Request/HttpModule2.elm
Request/HttpModule3.elm
```

## Getting started

To get started, first add project dependency to your `build.sbt`:

```scala
libraryDependencies += "io.scalaland" %% "endpoints-elm" % "0.9.2"
```

##### Endpoints definition

If you follow [endpoints quick start guide](http://julienrf.github.io/endpoints/quick-start.html),
you end up with something similar to:

```scala
import endpoints.{algebra, generic}

case class Counter(value: Int)
case class Increment(step: Int)

trait CounterEndpoints
    extends algebra.Endpoints
    with algebra.JsonSchemaEntities
    with algebra.JsonSchemas
    with generic.JsonSchemas {

  implicit lazy val counterSchema: JsonSchema[Counter] = named(genericJsonSchema[Counter], "Counter")
  implicit lazy val incrementSchema: JsonSchema[Increment] = named(genericJsonSchema[Increment], "Increment")
  
  val currentValue: Endpoint[Unit, Counter] =
    endpoint(
      get(path / "current-value"),
      jsonResponse[Counter](docs = Some("Coutner status")),
      tags = List("Counter")
    )

  val increment: Endpoint[Increment, Unit] =
    endpoint(
      post(path / "increment", jsonRequest[Increment](docs = Some("Counter increment request"))),
      emptyResponse(),
      tags = List("Counter")
    )
}
```

For code generation purposes it's required to:
- name your json schemas for types using `named(schema, "YourSchemaName")`
- tag your endpoints using `tags = List("YourEndpointTag")`

Type and file names in generated code is based on those names.

##### Mixing code generator interpreter

Then you need to mix in `io.scalaland.endpoints.elm.ElmCodeGenerator` with your endpoints trait.

```scala
import io.scalaland.endpoints.elm.ElmCodeGenerator

object ElmCounterGen extends CounterEndpoints with ElmCodeGenerator {

    def generateCode(): Unit = {
      writeElmCode("target/directory")(currentValue, increment)()    
    }
}
```

Invoking `ElmCounterGen.generateCode()` will clean target directory,
create `Data` and `Request` directories and write down elm modules for
data types and http api client.

## Codegen target

Generated code consists of bunch of modules of 2 kinds: data modules and http request modules.

##### Data modules

Example data module looks like follows:

```elm
{-
  This file was generated by endpoints-elm interpreter.
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

```

It contains:

- type definition (`type alias Counter`)
- initial/example value definition (`init`)
- Json codecs (`decoder` and `encoder`)
- utility functions for setting fields to a specific value (`setValue`)
- utility functions for updating fields using provided function (`updateValue`)


##### Http modules

On the other hand, HTTP module consists looks like this:

```elm
{-
  This file was generated by endpoints-elm interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.Counter exposing (..)

import Http
import HttpBuilder exposing (RequestBuilder)
import Json.Decode as Decode
import Bool.Extra
import Maybe.Extra

import Data.Counter exposing (..)
import Data.Increment exposing (..)


currentvalueGet : RequestBuilder Counter
currentvalueGet  =
  HttpBuilder.get "/current-value"
    |> HttpBuilder.withExpectJson Data.Counter.decoder
    |> HttpBuilder.withTimeout 30000


incrementPost : Increment -> RequestBuilder ()
incrementPost increment =
  HttpBuilder.post "/increment"
    |> HttpBuilder.withJsonBody (Data.Increment.encoder  increment)
    |> HttpBuilder.withExpect (Http.expectStringResponse (\_ -> Ok ()))
    |> HttpBuilder.withTimeout 30000
```

For each endpoint it contain a function that returns corresponding `RequestBuilder`.

##### Dependencies

Code generation in endpoints-elm makes some assumptions about dependencies in elm project.

You need to have installed following packages:

```
"Chadtech/elm-bool-extra": "2.4.0"
"NoRedInk/elm-json-decode-pipeline": "1.0.0"
"elm/core": "1.0.2"
"elm/http": "1.0.0"
"elm/json": "1.1.3"
"elm-community/maybe-extra": "5.0.0"
"lukewestby/elm-http-builder": "6.0.0"
```

See also example`out/elm.json`.


##### Customizing code generation

TBD
