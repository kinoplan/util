ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

// zzzzzzzzzzzzzzzzzzzz Common Modules zzzzzzzzzzzzzzzzzzzz

lazy val date = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common/date"))
  .configureCross(ModulesCommon.dateProfile)

lazy val http4sServer = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common/http4s/server"))
  .configureCross(ModulesCommon.http4sServerProfile)

lazy val integrationCheck = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common/integration-check"))
  .configureCross(ModulesCommon.integrationCheckProfile)

lazy val localesMinimalDb = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .enablePlugins(LocalesPlugin)
  .in(file("common/locales-minimal-db"))
  .configureCross(ModulesCommon.localesMinimalDbProfile)

lazy val logbackConfig = project
  .in(file("common/logback-config"))
  .configure(ModulesCommon.logbackConfigProfile)

lazy val nullableCore = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common/nullable/core"))
  .configureCross(ModulesCommon.nullableCoreProfile)

lazy val scalaLogging = project
  .in(file("common/scala-logging"))
  .configure(ModulesCommon.scalaLoggingProfile)

lazy val reactivemongoBase = project
  .in(file("common/reactivemongo/base"))
  .configure(ModulesCommon.reactivemongoBaseProfile)

lazy val reactivemongoBsonAny = project
  .in(file("common/reactivemongo/bson-any"))
  .configure(ModulesCommon.reactivemongoBsonAnyProfile)

lazy val reactivemongoBsonJodaTime = project
  .in(file("common/reactivemongo/bson-joda-time"))
  .configure(ModulesCommon.reactivemongoBsonJodaTimeProfile)

lazy val reactivemongoBsonRefined = project
  .in(file("common/reactivemongo/bson-refined"))
  .configure(ModulesCommon.reactivemongoBsonRefinedProfile)

lazy val redissonCore = project
  .in(file("common/redisson/core"))
  .configure(ModulesCommon.redissonProfile)

lazy val redissonCodecCirce = project
  .in(file("common/redisson/codec/circe"))
  .configure(ModulesCommon.redissonCodecCirceProfile)
  .dependsOn(redissonCore)

lazy val redissonCodecPlayJson = project
  .in(file("common/redisson/codec/play-json"))
  .configure(ModulesCommon.redissonCodecPlayJsonProfile)
  .dependsOn(redissonCore)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsAny = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/any"))
  .configureCross(ModulesImplicits.anyProfile)

lazy val implicitsBoolean = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/boolean"))
  .configureCross(ModulesImplicits.booleanProfile)

lazy val implicitsCollection = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/collection"))
  .configureCross(ModulesImplicits.collectionProfile)

lazy val implicitsJavaTime = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/date/java-time"))
  .configureCross(ModulesImplicits.javaTimeProfile)
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .dependsOn(date)

lazy val implicitsJodaTime = project
  .in(file("implicits/date/joda-time"))
  .configure(ModulesImplicits.jodaTimeProfile)
  .dependsOn(date.jvm)

// zzzzzzzzzzzzzzzzzzzz Play Modules zzzzzzzzzzzzzzzzzzzz

lazy val playErrorHandler = project
  .in(file("play/error-handler"))
  .configure(ModulesPlay.errorHandlerProfile)
  .dependsOn(scalaLogging, playRequestMapContext, playFiltersLogging % "test->test")

lazy val playFiltersLogging = project
  .in(file("play/filters/logging"))
  .configure(ModulesPlay.filtersLoggingProfile)
  .dependsOn(scalaLogging, playRequestMapContext)

lazy val playReactivemongo = project
  .in(file("play/reactivemongo"))
  .configure(ModulesPlay.reactivemongoProfile)
  .dependsOn(reactivemongoBase)

lazy val playRequestMapContext = project
  .in(file("play/request/map-context"))
  .configure(ModulesPlay.requestMapContextProfile)
  .dependsOn(scalaLogging)

// zzzzzzzzzzzzzzzzzzzz ZIO Modules zzzzzzzzzzzzzzzzzzzz

lazy val zioHttp4sHealthCheck = project
  .in(file("zio/http4s/healthcheck"))
  .configure(ModulesZio.http4sHealthcheckProfile)
  .dependsOn(zioIntegrationCheck.jvm)

lazy val zioIntegrationCheck = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("zio/integration-check"))
  .configure(ModulesZio.integrationCheckProfile)
  .dependsOn(integrationCheck)

lazy val zioMonitoringPrometheus = project
  .in(file("zio/monitoring/prometheus"))
  .configure(ModulesZio.monitoringPrometheusProfile)

lazy val zioReactivemongo = project
  .in(file("zio/reactivemongo"))
  .configure(ModulesZio.reactivemongoProfile)
  .dependsOn(zioIntegrationCheck.jvm, reactivemongoBase)

lazy val zioSttpLoggingSlf4j = project
  .in(file("zio/sttp/logging/slf4j"))
  .configure(ModulesZio.sttpLoggingSlf4jProfile)

lazy val zioTapirServer = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("zio/tapir/server"))
  .configure(ModulesZio.tapirServerProfile)

// format: off
inThisBuild(
  List(
    sonatypeCredentialHost := Sonatype.sonatype01,
    versionScheme := Some(VersionScheme.EarlySemVer),
    organization := "io.kinoplan",
    homepage := Some(url("https://github.com/kinoplan/utils")),
    licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0")),
    developers := List(Developer("kinoplan", "Kinoplan", "job@kinoplan.ru", url("https://kinoplan.tech"))),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/kinoplan/utils"),
        "scm:git:git@github.com:kinoplan/utils.git"
      )
    )
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges
// format: on
