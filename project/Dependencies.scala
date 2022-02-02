import sbt._

object Dependencies {

  object Versions {
    val reactivemongoV = "1.0.10"
    val zioV           = "1.0.13"
    val zioConfigV     = "2.0.0"
  }

  import Versions._

  // A -> Z
  val distageCore          = "io.7mind.izumi"             %% "distage-core"           % "1.0.9"
  val jodaTime             = "joda-time"                   % "joda-time"              % "2.10.13"
  val logback              = "ch.qos.logback"              % "logback-classic"        % "1.2.10"
  val logbackCore          = "ch.qos.logback"              % "logback-core"           % "1.2.10"
  val mockitoScala         = "org.scalatestplus"          %% "mockito-3-4"            % "3.2.10.0" % Test
  val reactiveMongo        = "org.reactivemongo"          %% "reactivemongo"          % reactivemongoV
  val scalaLogging         = "com.typesafe.scala-logging" %% "scala-logging"          % "3.9.3"
  val scalatest            = "org.scalatest"              %% "scalatest"              % "3.2.11"   % Test
  val typesafeConfig       = "com.typesafe"                % "config"                 % "1.4.2"
  val zio                  = "dev.zio"                    %% "zio"                    % zioV
  val zioConfig            = "dev.zio"                    %% "zio-config"             % zioConfigV
  val zioConfigTypesafe    = "dev.zio"                    %% "zio-config-typesafe"    % zioConfigV
  val zioHttp              = "io.d11"                     %% "zhttp"                  % "1.0.0.0-RC17"
  val zioLogging           = "dev.zio"                    %% "zio-logging"            % "0.5.14"
  val zioMetricsPrometheus = "dev.zio"                    %% "zio-metrics-prometheus" % "1.0.13"
  val zioPrelude           = "dev.zio"                    %% "zio-prelude"            % "1.0.0-RC8"
}
