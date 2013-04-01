import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "floodtweet"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc
    , "com.typesafe.slick" %% "slick" % "1.0.0"
    , "org.twitter4j" % "twitter4j-core" % "3.0.3"
    , "org.seleniumhq.selenium" % "selenium-java" % "2.28.0"
    , "org.quartz-scheduler" % "quartz" % "2.1.6"
    , "joda-time" % "joda-time" % "2.1"
    , "org.mockito" % "mockito-all" % "1.9.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(

    // Add your own project settings here
  )

}
