package io.scalaland.endpoints.elm.commons

import io.scalaland.endpoints.elm.model.CustomBasicType

object CustomTypes {

  val date = CustomBasicType(
    "Date",
    "Date.fromOrdinalDate 1970 1",
    "Encode.string << Date.toIsoString",
    """Decode.string |> Decode.andThen (Date.fromIsoString >> Result.map Decode.succeed >> Result.withDefault (Decode.fail "can't parse the date!"))""",
    "Date.toIsoString"
  )
}
