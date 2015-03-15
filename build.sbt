name in ThisBuild := "banana-rdf-example"

organization in ThisBuild := "org.w3"

version in ThisBuild := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.5"

scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-unchecked")

lazy val root = project.in(file("."))
  .aggregate(exampleJS, exampleJVM)
  .settings(
    publish := {},
    publishLocal := {}
  )

val bananaV = "0.8.1"

/* Enable FastOptStage if you have node.js installed. See:
 * - http://www.scala-js.org/doc/sbt/run.html
 * - http://www.scala-js.org/doc/sbt/js-envs.html
 */
//scalaJSStage in Global := FastOptStage

/* Documentation for cross-building with sbt-scalajs available at
 * http://www.scala-js.org/doc/sbt/cross-building.html
 * 
 * Depending on what you do, you would only pick the dependencies that
 * you need.
 */
lazy val example = crossProject
  .in(file("."))
  .settings(
    name := "example",
    // banana-rdf is the core dependency. It contains all the
    // abstractions and targets for the JVM and JS environments
    libraryDependencies += "org.w3" %%% "banana-rdf" % bananaV,
    // banana-rdf still has some dependencies that are not yet on Maven Central
    resolvers += Resolver.url("inthenow-releases", url("http://dl.bintray.com/inthenow/releases"))(Resolver.ivyStylePatterns),
    // an easy way to write tests for both environments
    // see https://github.com/lihaoyi/utest
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.1" % "test",
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    // banana-jena and banana-sesame defines instances for the
    // abstractions from banana-rdf
    libraryDependencies += "org.w3" %% "banana-jena" % bananaV,
    libraryDependencies += "org.w3" %% "banana-sesame" % bananaV,
    // plantain is a pure Scala implementation of the RDF abstractions
    libraryDependencies += "org.w3" %% "banana-plantain" % bananaV,
    // an HTTP Client just for the tests
    libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
  )
  .jsSettings(
    // banana-n3-js and banana-jsonld-js are JS-based instances for
    // banana-rdf's abstractions. They are based on the N3.js and
    // jsonld.js libraries
    libraryDependencies += "org.w3" %%% "banana-n3-js" % bananaV,
    libraryDependencies += "org.w3" %%% "banana-jsonld-js" % bananaV,
    // you can use the JS version of plantain as well
    libraryDependencies += "org.w3" %%% "banana-plantain" % bananaV,
    // this brings the XMLHttpRequest API
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  )


lazy val exampleJVM = example.jvm
lazy val exampleJS = example.js
