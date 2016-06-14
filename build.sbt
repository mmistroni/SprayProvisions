enablePlugins(JavaServerAppPackaging)

name := "spray-provision-service"

version := "0.1"

organization := "com.mm"

scalaVersion := "2.11.5"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  "Spray Repository"    at "http://repo.spray.io")

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
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0"
    
    
  )
}
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

// Assembly settings
mainClass in Global := Some("com.danielasfregola.quiz.management.Main")

jarName in assembly := "quiz-management-server.jar"