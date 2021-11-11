import sbt._

object Dependencies {
  //A -> Z
  val logback      = "ch.qos.logback"              % "logback-classic" % "1.2.7"
  val mockitoScala = "org.scalatestplus"          %% "mockito-3-4"     % "3.2.9.0" % Test
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.3"
  val scalatest    = "org.scalatest"              %% "scalatest"       % "3.2.9"   % Test
}
