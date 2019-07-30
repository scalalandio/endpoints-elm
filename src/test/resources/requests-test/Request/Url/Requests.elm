{-
  This file was generated by endpoints-elm 0.9.2 interpreter.
  Do not edit this file manually.

  See https://github.com/scalalandio/endpoints-elm for more information.
-}

module Request.Url.Requests exposing (..)

import Url.Builder
import Bool.Extra
import Maybe.Extra





jsonstringPost : String
jsonstringPost  =
  Url.Builder.relative
    ["/", "json-string"]
    ([])


jsonintPost : String
jsonintPost  =
  Url.Builder.relative
    ["/", "json-int"]
    ([])


jsonlongPost : String
jsonlongPost  =
  Url.Builder.relative
    ["/", "json-long"]
    ([])


jsonfloatPost : String
jsonfloatPost  =
  Url.Builder.relative
    ["/", "json-float"]
    ([])


jsondoublePost : String
jsondoublePost  =
  Url.Builder.relative
    ["/", "json-double"]
    ([])


jsonbooleanPost : String
jsonbooleanPost  =
  Url.Builder.relative
    ["/", "json-boolean"]
    ([])


jsonuuidPost : String
jsonuuidPost  =
  Url.Builder.relative
    ["/", "json-uuid"]
    ([])


jsondatePost : String
jsondatePost  =
  Url.Builder.relative
    ["/", "json-date"]
    ([])


jsoncaseclassPost : String
jsoncaseclassPost  =
  Url.Builder.relative
    ["/", "json-case-class"]
    ([])


jsoncoproductPost : String
jsoncoproductPost  =
  Url.Builder.relative
    ["/", "json-coproduct"]
    ([])


jsonlistPost : String
jsonlistPost  =
  Url.Builder.relative
    ["/", "json-list"]
    ([])


jsonmapPost : String
jsonmapPost  =
  Url.Builder.relative
    ["/", "json-map"]
    ([])
