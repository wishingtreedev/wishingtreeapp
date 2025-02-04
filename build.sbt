ThisBuild / scalaVersion := "3.6.3"

ThisBuild / organization := "app.wishingtree"

lazy val root = project
  .in(file("."))
  .aggregate(app)

lazy val app = project
  .in(file("app"))
  .settings(
    name := "wishingtree-app",
    libraryDependencies ++= Seq(
      "dev.wishingtree"             %% "branch"                  % "0.0.7",
      "com.softwaremill.sttp.tapir" %% "tapir-core"              % "1.11.13",
      "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % "1.11.13",
      "org.postgresql"               % "postgresql"              % "42.7.5"
    ) ++ Seq(
      "org.scalameta" %% "munit" % "1.1.0"
    ).map(_ % Test)
  )
