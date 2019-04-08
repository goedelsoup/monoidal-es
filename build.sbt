import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport._

ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "goedelsoup"

val CirisVersion = "0.12.1"
val Http4sVersion = "0.20.0-M4"
val LogbackVersion = "1.2.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)

lazy val mes = (project in file("."))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .settings(
    name := "mes",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "atto-core" % "0.6.5",

      "io.circe" %% "circe-generic" % "0.11.1",
      "io.circe" %% "circe-java8"   % "0.11.1",
      "io.circe" %% "circe-optics"  % "0.11.0",
      "io.circe" %% "circe-parser"  % "0.11.1",
      "io.circe" %% "circe-refined" % "0.11.1",

      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe"        % Http4sVersion,
      "org.http4s" %% "http4s-dsl"          % Http4sVersion,

      "io.chrisdavenport" %% "mules" % "0.2.0",

      "io.chrisdavenport" %% "log4cats-core"  % "0.3.0-M2",
      "io.chrisdavenport" %% "log4cats-slf4j" % "0.3.0-M2",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      "org.typelevel" %% "mouse" % "0.20",
      "org.typelevel"  %% "squants" % "1.4.0",

      "org.scalacheck" %% "scalacheck" % "1.14.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ) ++ Seq(
      "is.cir" %% "ciris-cats",
      "is.cir" %% "ciris-cats-effect",
      "is.cir" %% "ciris-core",
    ).map(_ % CirisVersion),

    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),

    initialCommands in console :=
      s"""import cats.implicits._
         |import mes._
         |import mes.domain._
         |import org.scalacheck._
       """.stripMargin,

    dockerBaseImage := "adoptopenjdk/openjdk10:jdk-10.0.2.13-alpine-slim",
    dockerUpdateLatest := true,
    packageName in Docker := "goedelsoup/mes",
    dockerExposedPorts += 9500,
    dockerCommands := Seq(
      Cmd("FROM", dockerBaseImage.value),
      Cmd("RUN", "apk add --no-cache bash"),
      Cmd("WORKDIR", "/opt/docker"),
      Cmd("ADD", "--chown=daemon:daemon opt /opt"),
      Cmd("USER", "daemon"),
      ExecCmd("ENTRYPOINT", dockerEntrypoint.value: _*),
      Cmd("CMD", "[]"))
  )
