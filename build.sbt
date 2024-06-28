name := """ralearn"""
organization := "com.example"
version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-java-ebean-example",
    version := "1.0.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.14", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "com.h2database" % "h2" % "2.2.224",
      "org.postgresql" % "postgresql" % "42.2.19",
      "org.awaitility" % "awaitility" % "3.1.6" % Test,
      "org.assertj" % "assertj-core" % "3.12.2" % Test,
      "org.mockito" % "mockito-core" % "5.12.0" % Test,
      "org.jsoup" % "jsoup" % "1.13.1",
      "org.json" % "json" % "20210307",
    ),
    (Test / testOptions) += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
