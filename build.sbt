name := "endpoints-elm"

version := "0.9.0"

organization := "io.scalaland"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-explaintypes",
  "-feature",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  //    "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint:adapted-args",
  "-Xlint:delayedinit-select",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",

  "-Xexperimental",
  "-Yno-adapted-args",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xlint:by-name-right-associative",
  "-Xlint:unsound-match"
)

scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")


val endpointsVersion = "0.9.0"

libraryDependencies ++= Seq(
  "org.julienrf" %% "endpoints-algebra" % endpointsVersion % "provided",
  "org.julienrf" %% "endpoints-algebra-json-schema" % endpointsVersion % "provided",
)


homepage := Some(url("https://github.com/scalalandio"))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

scmInfo := Some(
  ScmInfo(url("https://github.com/scalalandio/endpoints-elm"), "scm:git:git@github.com:scalalandio/endpoints-elm.git")
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <developers>
    <developer>
      <id>krzemin</id>
      <name>Piotr Krzemiński</name>
      <url>http://github.com/krzemin</url>
    </developer>
    <developer>
      <id>MateuszKubuszok</id>
      <name>Mateusz Kubuszok</name>
      <url>http://github.com/MateuszKubuszok</url>
    </developer>
  </developers>
  )
