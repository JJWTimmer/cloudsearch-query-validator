import ReleaseTransformations._

name := """cloudsearch-query-validator"""

scalaVersion := "2.11.7"

packageSummary := "CloudSearch Structered Query Validator"

packageDescription := "CloudSearch Structered Query Validator using FastParse"

maintainer := "Jasper Timmer <jjwtimmer@gmail.com>"

organization := "com.github.jjwtimmer"

//uncomment the following line if you want cross build
// crossScalaVersions := Seq("2.10.4", "2.11.6")

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

releasePublishArtifactsAction := PgpKeys.publishSigned.value

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/jjwtimmer/cloudsearch-query-validator</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://www.opensource.org/licenses/bsd-license.php</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jjwtimmer/cloudsearch-query-validator.git</url>
      <connection>scm:git:git@github.com:jjwtimmer/cloudsearch-query-validator.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jjwtimmer</id>
        <name>Jasper Timmer</name>
      </developer>
    </developers>

scalacOptions ++=  Seq(
  "-deprecation",
  "-unchecked",
  "-feature"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.3.7",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

scalariformSettings

//uncomment the following line if you want a java app packaging
// enablePlugins(JavaAppPackaging)
// enablePlugins(UniversalPlugin)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)


fork in run := true