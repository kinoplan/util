import sbt._

object Dependencies {

  object Versions {
    val reactivemongoV = "1.0.7"
    val zioV           = "1.0.12"
    val zioConfigV     = "1.0.10"
  }

  import Versions._

  //A -> Z
  val distageCore       = "io.7mind.izumi"             %% "distage-core"        % "1.0.8"
  val jodaTime          = "joda-time"                   % "joda-time"           % "2.10.13"
  val logback           = "ch.qos.logback"              % "logback-classic"     % "1.2.7"
  val mockitoScala      = "org.scalatestplus"          %% "mockito-3-4"         % "3.2.10.0" % Test
  val reactiveMongo     = "org.reactivemongo"          %% "reactivemongo"       % reactivemongoV
  val scalaLogging      = "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.3"
  val scalatest         = "org.scalatest"              %% "scalatest"           % "3.2.10"   % Test
  val zio               = "dev.zio"                    %% "zio"                 % zioV
  val zioConfig         = "dev.zio"                    %% "zio-config"          % zioConfigV
  val zioConfigTypesafe = "dev.zio"                    %% "zio-config-typesafe" % zioConfigV
  val zioPrelude        = "dev.zio"                    %% "zio-prelude"         % "1.0.0-RC8"
}
