package io.scalaland.endpoints.elm.emit

import java.io.{File, PrintWriter}

object FileUtils {

  def cleanDirectory(file: File, remove: Boolean = false): Unit = {
    if (file.isDirectory) {
      file.listFiles.foreach(cleanDirectory(_, remove = true))
    }
    if (remove && file.exists && !file.delete) {
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
  }

  def generateCode(targetDir: File, pathContents: Seq[(File, String)], log: File => Unit = _ => ()): Unit = {
    pathContents.foreach {
      case (file, content) =>
        val targetPath = new File(targetDir, file.getPath)
        targetPath.getParentFile.mkdirs()
        writeFile(targetPath, content)
        log(file)
    }
  }

  def writeFile(file: File, content: String): Unit = {
    val pw = new PrintWriter(file)
    try {
      pw.println(content)
    } finally {
      pw.close()
    }
  }
}
