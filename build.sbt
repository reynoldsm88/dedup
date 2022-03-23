import Dependencies._
import sbt._

organization in ThisBuild := "com.twosixlabs.dart"
name := "dedup"
scalaVersion in ThisBuild := "2.12.7"

resolvers in ThisBuild ++= Seq( "Maven Central" at "https://repo1.maven.org/maven2/",
                                "JCenter" at "https://jcenter.bintray.com" )

lazy val root = ( project in file( "." ) ).settings( libraryDependencies ++= chronicleMap
                                                                             ++ betterFiles
                                                                             ++ scalaTest )

publishMavenStyle := true

sonatypeProfileName := "com.twosixlabs"
inThisBuild(
    List(
        organization := organization.value,
        homepage := Some( url( "https://github.com/twosixlabs-dart/dedup" ) ),
        licenses := List( "Apache License 2.0" -> url( "https://www.apache.org/licenses/LICENSE-2.0.html" ) ),
        developers := List( Developer( "twosixlabs-dart", "Two Six Technologies", "", url( "https://github.com/twosixlabs-dart" ) ) )
    )
)

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
