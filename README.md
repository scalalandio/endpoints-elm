# endpoints-elm

[![Build Status](https://travis-ci.org/scalalandio/endpoints-elm.svg?branch=master)](https://travis-ci.org/scalalandio/endpoints-elm)
[![Maven Central](https://img.shields.io/maven-central/v/io.scalaland/endpoints-elm_2.12.svg)](http://search.maven.org/#search%7Cga%7C1%7Cendpoints-elm)
[![Javadocs](https://www.javadoc.io/badge/io.scalaland/endpoints-elm_2.12.svg?color=red&label=scaladoc)](https://www.javadoc.io/doc/io.scalaland/endpoints-elm_2.12)
[![codecov.io](http://codecov.io/github/scalalandio/endpoints-elm/coverage.svg?branch=master)](http://codecov.io/github/scalalandio/endpoints-elm?branch=master)
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

TBD
