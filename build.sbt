import sbt.addCompilerPlugin

name := "zrule"
organization  := "org.dzavolskaya"

Global / cancelable := true

version := "0.1"

scalaVersion := "2.13.7"

val parserCombinatorsVersion = "2.1.0"
val http4sVersion = "0.21.4"
val circeVersion = "0.13.0"
val doobieVersion = "0.13.4"

val apiDependencies = Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",
  "org.http4s" %% "http4s-core" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.monix" %% "monix" % "3.2.1",
  "org.apache.commons" % "commons-lang3" % "3.11",
)

val testDependencies = Seq(
  "org.scalactic" %% "scalactic" % "3.2.10",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  "io.zonky.test" % "embedded-postgres" % "1.3.1" % "test",
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.7.0",
  "org.typelevel" %% "cats-free" % "2.7.0",
  "org.typelevel" %% "cats-effect" % "2.1.3",
  "org.typelevel" %% "cats-mtl-core" % "0.7.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.10",
  "org.scala-lang.modules" %% "scala-parser-combinators" % parserCombinatorsVersion,
) ++ testDependencies ++ apiDependencies

scalacOptions ++= Seq(
  "-deprecation",                  // Emit warning and location for usages of deprecated APIs
  "-encoding", "utf-8",            // Specify character encoding used by source files
  "-explaintypes",                 // Explain type errors in more detail
  "-feature",                      // Emit warning and location for usages of features that should be imported explicitly
  "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds",         // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-language:postfixOps",          // Allow post fix ops
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions
  "-Wdead-code",                   // Warn when dead code is identified.
  "-Wextra-implicit",              // Warn when more than one implicit parameter section is defined.
  "-Wmacros:both",                 // Lints code before and after applying a macro
  "-Wnumeric-widen",               // Warn when numerics are widened
  "-Wunused:imports",              // Warn if an import selector is not referenced
  "-Wunused:patvars",              // Warn if a variable bound in a pattern is unused
  "-Wvalue-discard",               // Warn when non-Unit expression results are unused
  "-Ymacro-annotations",
)
