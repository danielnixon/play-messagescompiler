sbtPlugin := true

name := "play-messagescompiler"

organization := "com.tegonal"

version := "1.0.7-SNAPSHOT"

description := "SBT plugin for compiled messages resources in Play 2.6"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers ++= Seq(
  Resolver.url("Typesafe repository", url("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns),
  "Typesafe Releases Maven " at "http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-M1")

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.8.8" % Test
  )

scalacOptions += "-deprecation"

initialCommands := "import com.tegonal.play-messagescompiler._"
