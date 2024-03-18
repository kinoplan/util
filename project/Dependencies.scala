import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
import sbt.*
import sbt.librarymanagement.DependencyBuilders

object Dependencies {

  object Versions {
    val circeV         = "0.14.6"
    val playV          = "2.8.21"
    val reactivemongoV = "1.0.10"
    val scalaJavaTimeV = "2.5.0"
    val sttpV          = "3.9.4"
    val tapirV         = "1.10.0"
    val zioConfigV     = "4.0.1"
  }

  import Versions.*

  object Libraries {
    val scalaReflect = "org.scala-lang" % "scala-reflect"
    // Cross-platform dependencies
    val circeCore         = Def.setting("io.circe" %%% "circe-core" % circeV)
    val circeGeneric      = Def.setting("io.circe" %%% "circe-generic" % circeV)
    val localesFullDb     = Def.setting("io.github.cquiroz" %%% "locales-full-db" % "1.5.1")
    val scalaJavaLocales  = Def.setting("io.github.cquiroz" %%% "scala-java-locales" % "1.5.1")
    val scalaJavaTime     = Def.setting("io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeV)
    val scalaJavaTimeZone = Def.setting("io.github.cquiroz" %%% "scala-java-time-tzdb" % scalaJavaTimeV)
    val scalatest         = Def.setting("org.scalatest" %%% "scalatest" % "3.2.18" % Test)
    val tapirCore         = Def.setting("com.softwaremill.sttp.tapir" %%% "tapir-core" % tapirV)

    // A -> Z
    val circeParser           = "io.circe"                      %% "circe-parser"            % circeV
    val http4sBlazeServer     = "org.http4s"                    %% "http4s-blaze-server"     % "0.23.16"
    val http4sDsl             = "org.http4s"                    %% "http4s-dsl"              % "0.23.26"
    val http4sServer          = "org.http4s"                    %% "http4s-server"           % "0.23.26"
    val jacksonModule         = "com.fasterxml.jackson.module"  %% "jackson-module-scala"    % "2.17.0"
    val jodaTime              = "joda-time"                      % "joda-time"               % "2.12.7"
    val kindProjector         = "org.typelevel"                 %% "kind-projector"          % "0.13.3"
    val logback               = "ch.qos.logback"                 % "logback-classic"         % "1.2.13"
    val logbackCore           = "ch.qos.logback"                 % "logback-core"            % "1.2.13"
    val mockitoScala          = "org.scalatestplus"             %% "mockito-3-4"             % "3.2.10.0" % Test
    val play                  = "com.typesafe.play"             %% "play"                    % playV
    val playJson              = "com.typesafe.play"             %% "play-json"               % "2.10.4"
    val playReactiveMongo     = "org.reactivemongo"             %% "play2-reactivemongo"     % s"$reactivemongoV-play28"
    val reactiveMongo         = "org.reactivemongo"             %% "reactivemongo"           % reactivemongoV
    val redisson              = "org.redisson"                   % "redisson"                % "3.24.2"
    val refined               = "eu.timepit"                    %% "refined"                 % "0.11.1"
    val scalaCollectionCompat = "org.scala-lang.modules"        %% "scala-collection-compat" % "2.11.0"
    val scalaLogging          = "com.typesafe.scala-logging"    %% "scala-logging"           % "3.9.5"
    val scalatestPlay         = "org.scalatestplus.play"        %% "scalatestplus-play"      % "5.1.0"    % Test
    val sourcecode            = "com.lihaoyi"                   %% "sourcecode"              % "0.3.1"
    val sttpSlf4jBackend      = "com.softwaremill.sttp.client3" %% "slf4j-backend"           % sttpV
    val tapirServer           = "com.softwaremill.sttp.tapir"   %% "tapir-server"            % tapirV
    val typesafeConfig        = "com.typesafe"                   % "config"                  % "1.4.3"
    val zio                   = "dev.zio"                       %% "zio"                     % "2.0.21"
    val zioConfig             = "dev.zio"                       %% "zio-config"              % zioConfigV
    val zioConfigTypesafe     = "dev.zio"                       %% "zio-config-typesafe"     % zioConfigV
    val zioConfigMagnolia     = "dev.zio"                       %% "zio-config-magnolia"     % zioConfigV
    val zioInteropCats        = "dev.zio"                       %% "zio-interop-cats"        % "23.0.0.8"
    val zioMetricsPrometheus  = "dev.zio"                       %% "zio-metrics-prometheus"  % "2.0.1"
  }

  object Batches {
    val zioConfig: Seq[ModuleID] = Seq(Libraries.zioConfig, Libraries.zioConfigTypesafe, Libraries.zioConfigMagnolia)
  }

  case class ShadingEntity(
    dependencies: Seq[sbt.ModuleID],
    modulePackages: Seq[String],
    notShadedDependencies: Seq[sbt.ModuleID],
    notShadedScalaVersionDependencies: Seq[DependencyBuilders.OrganizationArtifactName]
  ) {

    def libraryDependencies(scalaVersion: String): Seq[ModuleID] = dependencies ++ notShadedDependencies ++
      notShadedScalaVersionDependencies.map(_ % scalaVersion)

  }

  object Shades {

    val zioConfig: ShadingEntity = ShadingEntity(
      Batches.zioConfig,
      Seq("zio.config", "com.typesafe.config", "mercator", "magnolia"),
      Seq(Libraries.scalaCollectionCompat),
      Seq(Libraries.scalaReflect)
    )

  }

}
