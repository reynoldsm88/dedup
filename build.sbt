import Dependencies._
import sbt._

organization in ThisBuild := "com.github.reynoldsm88"
name := "dedup"
version in ThisBuild := "1.0.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.7"

resolvers in ThisBuild ++= Seq( "Maven Central" at "https://repo1.maven.org/maven2/",
                                "JCenter" at "https://jcenter.bintray.com" )

lazy val root = ( project in file( "." ) ).settings( libraryDependencies ++= chronicleMap
                                                                             ++ betterFiles
                                                                             ++ scalaTest )

publishMavenStyle := true

githubTokenSource := TokenSource.GitConfig( "github.token" ) || TokenSource.Environment( "GITHUB_TOKEN" )
githubOwner := "reynoldsm88"
githubRepository := "dedup"
