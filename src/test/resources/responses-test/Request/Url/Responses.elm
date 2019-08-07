{-
  This file was generated by endpoints-elm 0.10.0 interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.Url.Responses exposing (..)

import Url.Builder
import Bool.Extra
import Maybe.Extra





jsonstringGet : String
jsonstringGet  =
  Url.Builder.relative
    ["/", "json-string"]
    ([])


jsonintGet : String
jsonintGet  =
  Url.Builder.relative
    ["/", "json-int"]
    ([])


jsonlongGet : String
jsonlongGet  =
  Url.Builder.relative
    ["/", "json-long"]
    ([])


jsonfloatGet : String
jsonfloatGet  =
  Url.Builder.relative
    ["/", "json-float"]
    ([])


jsondoubleGet : String
jsondoubleGet  =
  Url.Builder.relative
    ["/", "json-double"]
    ([])


jsonbooleanGet : String
jsonbooleanGet  =
  Url.Builder.relative
    ["/", "json-boolean"]
    ([])


jsonuuidGet : String
jsonuuidGet  =
  Url.Builder.relative
    ["/", "json-uuid"]
    ([])


jsondateGet : String
jsondateGet  =
  Url.Builder.relative
    ["/", "json-date"]
    ([])


jsoncaseclassGet : String
jsoncaseclassGet  =
  Url.Builder.relative
    ["/", "json-case-class"]
    ([])


jsoncoproductGet : String
jsoncoproductGet  =
  Url.Builder.relative
    ["/", "json-coproduct"]
    ([])


jsonlistGet : String
jsonlistGet  =
  Url.Builder.relative
    ["/", "json-list"]
    ([])


jsonmapGet : String
jsonmapGet  =
  Url.Builder.relative
    ["/", "json-map"]
    ([])

