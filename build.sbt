name := """tictactoe-server"""
organization := "io.eschmann"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.2"
