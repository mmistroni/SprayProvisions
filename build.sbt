enablePlugins(JavaServerAppPackaging)

name := "spray-provision-service"

version := "0.1"

organization := "com.mm"

scalaVersion := "2.11.5"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  "Spray Repository"    at "http://repo.spray.io",
                  "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")

libraryDependencies ++= {
  val AkkaVersion       = "2.3.9"
  val SprayVersion      = "1.3.2"
  val Json4sVersion     = "3.2.11"
  Seq(
    "io.spray"          %% "spray-can"       % SprayVersion,
    "io.spray"          %% "spray-routing"   % SprayVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % AkkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.2",
    "com.h2database" % "h2" % "1.4.190",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.specs2" %% "specs2" % "2.3.11" % "test",
    "org.json4s"        %% "json4s-native"   % Json4sVersion,
    "org.json4s"        %% "json4s-ext"      % Json4sVersion,
    "io.spray" %% "spray-testkit" % SprayVersion  % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "joda-time" % "joda-time" % "2.9.3",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
    "mysql" % "mysql-connector-java" % "5.1.38",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
    "com.typesafe.slick" %% "slick-codegen" % "3.1.1"
    
  )
}
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

// Assembly settings
mainClass in Global := Some("com.mm.spray.provision.Main")

jarName in assembly := "spray-provisionApp.jar"

assemblyMergeStrategy in assembly := {
  case PathList("javax", "mail", xs @ _*)         => MergeStrategy.first
  case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
