import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo" %% "play2-reactivemongo"           % "0.18.8-play27",
    "uk.gov.hmrc"       %% "logback-json-logger"           % "5.1.0",
    "uk.gov.hmrc"       %% "play-health"                   % "3.16.0-play-27",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.6.0-play-27",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-27"    % "3.1.0",
    "uk.gov.hmrc"       %% "play-language"                 % "4.10.0-play-27",
    "uk.gov.hmrc"       %% "play-nunjucks"                 % "0.26.0-play-27",
    "uk.gov.hmrc"       %% "play-nunjucks-viewmodel"       % "0.12.0-play-27",
    "org.webjars.npm"   % "govuk-frontend"                 % "3.11.0",
    "org.webjars.npm"   % "hmrc-frontend"                  % "1.27.0"
  )

  val test = Seq(
    "org.scalatest"               %% "scalatest"             % "3.2.5",
    "org.scalatestplus.play"      %% "scalatestplus-play"    % "4.0.3",
    "org.pegdown"                 %  "pegdown"               % "1.6.0",
    "org.jsoup"                   %  "jsoup"                 % "1.13.1",
    "com.typesafe.play"           %% "play-test"             % PlayVersion.current,
    "wolfendale"                 %% "scalacheck-gen-regexp"    % "0.1.1",
    "org.mockito"                 % "mockito-core"           % "3.8.0",
    "org.scalatestplus"           %% "mockito-3-2"           % "3.1.2.0",
    "org.scalacheck"              %% "scalacheck"            % "1.14.3",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "com.github.tomakehurst"      % "wiremock-standalone"       % "2.27.1",
    "com.vladsch.flexmark"        % "flexmark-all"              % "0.36.8"

  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.7"
  val akkaHttpVersion = "10.1.12"

  val overrides = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12"    % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12"  % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12"     % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12"     % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12" % akkaHttpVersion
  )
}
