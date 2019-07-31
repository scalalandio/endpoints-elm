module EndpointsElm exposing
    ( httpResolveBytes
    , httpResolveJson
    , httpResolveNotFound
    , httpResolveString
    , httpResolveUnit
    )

import Bytes exposing (Bytes)
import Http
import Json.Decode exposing (Decoder)


httpResolveNotFound : (Http.Response a -> Result Http.Error b) -> Http.Response a -> Result Http.Error (Maybe b)
httpResolveNotFound resolveOriginal response =
    case response of
        Http.BadStatus_ metadata _ ->
            if metadata.statusCode == 404 then
                Ok Nothing

            else
                resolveOriginal response |> Result.map Just

        _ ->
            resolveOriginal response |> Result.map Just


httpResolveUnit : Http.Response a -> Result Http.Error ()
httpResolveUnit =
    httpResolve >> Result.map (\_ -> ())


httpResolveJson : Decoder a -> Http.Response String -> Result Http.Error a
httpResolveJson decoder =
    httpResolveString
        >> Result.andThen
            (Json.Decode.decodeString decoder
                >> Result.mapError (Json.Decode.errorToString >> Http.BadBody)
            )


httpResolveString : Http.Response String -> Result Http.Error String
httpResolveString =
    httpResolve


httpResolveBytes : Http.Response Bytes -> Result Http.Error Bytes
httpResolveBytes =
    httpResolve


httpResolve : Http.Response a -> Result Http.Error a
httpResolve response =
    case response of
        Http.BadUrl_ url ->
            Err (Http.BadUrl url)

        Http.Timeout_ ->
            Err Http.Timeout

        Http.NetworkError_ ->
            Err Http.Timeout

        Http.BadStatus_ metadata _ ->
            Err (Http.BadStatus metadata.statusCode)

        Http.GoodStatus_ _ body ->
            Result.mapError Http.BadBody (Ok body)

