ThisBuild / scalaVersion := "3.6.3"

ThisBuild / organization := "app.wishingtree"

ThisBuild / scalacOptions ++= Seq(
  "-no-indent",
  "-rewrite",
  "-source:3.4-migration"
)

lazy val root = project
  .in(file("."))
  .settings(
    name            := "wishingtree",
    publishArtifact := false
  )
  .aggregate(app)

lazy val app = project
  .in(file("app"))
  .settings(
    name := "app",
    libraryDependencies ++= Seq(
      "dev.wishingtree"             %% "branch"                  % "0.0.7",
      "com.softwaremill.sttp.tapir" %% "tapir-core"              % "1.11.13",
      "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % "1.11.13",
      "org.postgresql"               % "postgresql"              % "42.7.5"
    ) ++ Seq(
      "org.scalameta" %% "munit" % "1.1.0"
    ).map(_ % Test)
  )
