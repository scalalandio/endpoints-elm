package io.scalaland.endpoints.elm

import java.io.File

import io.scalaland.endpoints.elm.emit._
import io.scalaland.endpoints.elm.model._

trait ElmCodeGenerator extends Endpoints with JsonSchemaEntities with JsonSchemas {

  def writeElmCode(targetDirectory: String, clean: Boolean = true)(endpoints: ElmEndpoint*)(
    httpApiUrlPrefix: String = "",
    withCredentials: Boolean = false,
    additionalContents: Seq[(File, String)] = Seq.empty
  ): Unit = {

    if (clean) {
      FileUtils.cleanDirectory(new File(targetDirectory))
    }

    val fileContents = generateElmContents(endpoints: _*)(httpApiUrlPrefix, withCredentials) ++ additionalContents

    FileUtils.generateCode(new File(targetDirectory), fileContents)
  }

  def generateElmContents(endpoints: ElmEndpoint*)(urlPrefix: String = "",
                                                   withCredentials: Boolean = false): Seq[(File, String)] = {

    val commonFiles = Seq(new File("EndpointsElm.elm") -> scala.io.Source.fromResource("elm/EndpointsElm.elm").mkString)

    val typeFiles = captureEndpointsTypes(endpoints).map { elmType =>
      new File(s"Data/${elmType.name}.elm") -> TypeEmit.moduleDefinition(elmType)
    }

    val emitCtx = emit.Context(urlPrefix, withCredentials)

    val httpFiles = groupEndpointsIntoModules(endpoints).map { httpModule =>
      new File(s"Request/${httpModule.name}.elm") -> HttpEmit.moduleDefinition(httpModule)(emitCtx)
    }

    val urlFiles = groupEndpointsIntoModules(endpoints).map { httpModule =>
      new File(s"Request/Url/${httpModule.name}.elm") -> UrlEmit.moduleDefinition(httpModule)(emitCtx)
    }

    commonFiles ++ typeFiles ++ httpFiles ++ urlFiles
  }

  private def captureEndpointsTypes(endpoints: Seq[ElmEndpoint]): Seq[ElmType] = {
    endpoints
      .flatMap(HttpEmit.endpointReferencedTypes)
      .flatMap(ElmType.referencesDeep)
      .distinct
      .sortBy(_.name)
  }

  private def groupEndpointsIntoModules(endpoints: Seq[ElmEndpoint]): Seq[ElmHttpModule] = {
    endpoints
      .flatMap { endpoint =>
        endpoint.tags.map { tag =>
          tag -> endpoint
        }
      }
      .groupBy(_._1)
      .mapValues(_.map(_._2))
      .map {
        case (tag, moduleEndpoints) =>
          ElmHttpModule(NameUtils.tagToModuleName(tag), moduleEndpoints)
      }
      .toSeq
  }
}
