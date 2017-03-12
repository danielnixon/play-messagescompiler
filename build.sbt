sbtPlugin := true

name := "play-messagescompiler"

organization := "org.danielnixon"

version := "1.0.10-SNAPSHOT"

description := "SBT plugin for compiled messages resources in Play 2.6"

licenses := Seq("GNU General Public License (GPL), Version 3" -> url("http://www.gnu.org/licenses/gpl.txt"))
publishMavenStyle := true
publishArtifact in Test := false
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
homepage := Some(url("https://github.com/danielnixon/play-messagescompiler"))
pomExtra := {
  <scm>
    <url>git@github.com:danielnixon/play-messagescompiler.git</url>
    <connection>scm:git:git@github.com:danielnixon/play-messagescompiler.git</connection>
  </scm>
    <developers>
      <developer>
        <id>danielnixon</id>
        <name>Daniel Nixon</name>
        <url>https://danielnixon.org/</url>
      </developer>
    </developers>
}

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-M2")

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.8.9" % Test
  )

scalacOptions += "-deprecation"

initialCommands := "import com.tegonal.play-messagescompiler._"
