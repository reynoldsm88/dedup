import Dependencies._
import sbt._

organization in ThisBuild := "com.github.reynoldsm88"
name := "dedup"

scalaVersion in ThisBuild := "2.12.7"

resolvers in ThisBuild ++= Seq( "Maven Central" at "https://repo1.maven.org/maven2/",
                                "JCenter" at "https://jcenter.bintray.com" )

lazy val root = ( project in file( "." ) ).settings( libraryDependencies ++= chronicleMap
                                                                             ++ betterFiles
                                                                             ++ scalaTest )
