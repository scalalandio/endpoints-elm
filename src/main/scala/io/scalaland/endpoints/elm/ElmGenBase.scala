//package io.scalaland.endpoints.elm
//
//import java.io.File
//
//import io.scalaland.endpoints.elm.ast._
//import io.scalaland.endpoints.elm.emit.{HttpEmit, TypeEmit}
//
//import scala.util.Try
//
//class ElmGenBase
//  extends Endpoints
//    with JsonSchemaEntities
//    with JsonSchemas
//    with BasicAuthentication {
//
//  def elmGenCodeProgram(args: Array[String],
//                        additionalContent: Seq[(File, String)] = Seq.empty)
//                       (endpoints: ElmEndpoint*): Unit = {
//    args match {
//      case Array(targetDir, apiHttpPrefix, withCredentialsStr) =>
//
//        val withCredentials = Try(withCredentialsStr.toBoolean).getOrElse(false)
//
//        val target = new File(targetDir)
//
//        println(s"Cleaning $targetDir...")
//        Codegen.cleanDirectory(target)
//
//        println(s"Starting code generation for elm 0.19 with api prefix $apiHttpPrefix (withCredentials=$withCredentials)")
//
//        Codegen.generateCode(
//          target,
//          generateElmCode(apiHttpPrefix, withCredentials)(endpoints : _*) ++ additionalContent,
//          f => println(s"Wrote ${f.getPath}")
//        )
//
//        println(s"Code generation in $target done.")
//
//      case _ =>
//        println("Target codegen directory must be passed as an argument!")
//        System.exit(1)
//    }
//  }
//
//  def generateElmCode(urlPrefix: String, withCredentials: Boolean)
//                     (endpoints: ElmEndpoint*): Seq[(File, String)] = {
//
//    val typeFiles = captureEndpointsTypes(endpoints).map { elmType =>
//      new File(s"Data/${elmType.name}.elm") -> TypeEmit.moduleDefinition(elmType)
//    }
//
//    val emitCtx = emit.Context(urlPrefix, withCredentials)
//
//    val httpFiles = groupEndpointsIntoModules(endpoints).map { httpModule =>
//      new File(s"Request/${httpModule.name}.elm") -> HttpEmit.moduleDefinition(httpModule)(emitCtx)
//    }
//
//    typeFiles ++ httpFiles
//  }
//
//  private def captureEndpointsTypes(endpoints: Seq[ElmEndpoint]): Seq[ElmType] = {
//    endpoints
//      .flatMap(HttpEmit.endpointReferencedTypes)
//      .flatMap(ElmType.referencesDeep)
//      .distinct
//      .sortBy(_.name)
//  }
//
//  private def groupEndpointsIntoModules(endpoints: Seq[ElmEndpoint]): Seq[ElmHttpModule] = {
//    endpoints
//      .flatMap { endpoint =>
//        endpoint.tags.map { tag =>
//          tag -> endpoint
//        }
//      }
//      .groupBy(_._1)
//      .mapValues(_.map(_._2))
//      .map { case (tag, moduleEndpoints) =>
//        val moduleName = tag.split("[/\\s+]").map(_.capitalize).mkString
//        ElmHttpModule(moduleName, moduleEndpoints)
//      }
//      .toSeq
//  }
//}
