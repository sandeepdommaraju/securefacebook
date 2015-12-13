name := "securefacebook"

version       := "0.1"

scalaVersion  := "2.11.7"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"       % sprayV,
    "io.spray"            %%  "spray-routing"   % sprayV,
    "io.spray"            %%  "spray-testkit"   % sprayV  % "test",
    "io.spray"            %%  "spray-client"    % sprayV,
    "org.json4s"          %%  "json4s-native"   % "3.3.0",
    "io.spray"            %%  "spray-json"      % "1.3.2",
    "com.typesafe.akka"   %%  "akka-actor"      % akkaV,
    "com.etaty.rediscala" %%  "rediscala"       % "1.5.0",
    "com.typesafe.akka"   %%  "akka-testkit"    % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"     % "2.3.11" % "test",
    "net.liftweb"         %%  "lift-json"       % "2.6+",
    "commons-codec"       %   "commons-codec"   % "1.10"
  )
}

