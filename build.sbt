ThisBuild / scalaVersion := "2.13.14"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """HolaMundo""",
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play-slick" % "5.3.0", // Versión de Play Slick
      "com.typesafe.play" %% "play-slick-evolutions" % "5.3.0", // Para usar evoluciones si es necesario
      "org.postgresql" % "postgresql" % "42.7.3" // Driver PostgreSQL
    )
  )
