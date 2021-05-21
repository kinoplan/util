resolvers in ThisBuild += "Artima Maven Repository".at("https://repo.artima.com/releases")

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.12")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.8.0")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.18")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.27")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")
