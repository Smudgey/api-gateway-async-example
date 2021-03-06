import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning
import play.sbt.PlayImport._

object MicroServiceBuild extends Build with MicroService {
  import play.sbt.routes.RoutesKeys._

  val appName = "api-gateway-async-example"

  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()
  override lazy val playSettings : Seq[Setting[_]] = Seq(routesImport ++= Seq("uk.gov.hmrc.apigatewayexample.binder.Binders._"))
}

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val microserviceBootstrapVersion = "6.11.0"
  private val authClientVersion = "2.4.0"
  private val domainVersion = "5.0.0"
  private val playHmrcApiVersion = "2.0.0"
  private val hmrcTestVersion = "3.0.0"
  private val pegdownVersion = "1.6.0"
  private val scalaTestVersion = "2.2.6"
  private val wireMockVersion = "2.2.2"
  private val cucumberVersion = "1.2.5"
  private val microserviceAsync = "2.0.0"
  private val scalaMockVersion = "4.0.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % microserviceBootstrapVersion,
    "uk.gov.hmrc" %% "auth-client" % authClientVersion,
    "uk.gov.hmrc" %% "microservice-async" % microserviceAsync,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "uk.gov.hmrc" %% "play-hmrc-api" % playHmrcApiVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % "test,it",
        "org.scalatest" %% "scalatest" % scalaTestVersion % "test,it",
        "org.pegdown" % "pegdown" % pegdownVersion % "test,it",
        "com.typesafe.play" %% "play-test" % PlayVersion.current % "test,it",
        "org.scalamock" %% "scalamock" % scalaMockVersion % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc" %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}
