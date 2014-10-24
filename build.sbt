name := "FeedsAPI"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
			"org.scalatest" % "scalatest_2.10" % "2.2.0",
			"org.scala-lang" % "scala-library" % "2.10.4",
			"org.scala-lang" % "scala-compiler" % "2.10.4",
			"org.jsoup" % "jsoup" % "1.7.3",
			"edu.uci.ics" % "crawler4j" % "3.5",
			"com.google.code.maven-play-plugin.org.playframework" % "play" % "1.2.7"
)
