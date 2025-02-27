/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject()(configuration: Configuration) {

  val contactHost: String                  = configuration.get[String]("contact-frontend.host")
  val contactFormServiceIdentifier: String = "CTCTrader"

  val trackingConsentUrl: String = configuration.get[String]("microservice.services.tracking-consent-frontend.url")
  val gtmContainer: String       = configuration.get[String]("microservice.services.tracking-consent-frontend.gtm.container")

  val analyticsToken: String         = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String          = configuration.get[String](s"google-analytics.host")
  val betaFeedbackUrl                = s"$contactHost/contact/beta-feedback"
  val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"
  val signOutUrl: String             = configuration.get[String]("urls.logoutContinue") + configuration.get[String]("urls.feedback")
  lazy val nctsEnquiriesUrl: String  = configuration.get[String]("urls.nctsEnquiries")

  lazy val authUrl: String                = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String               = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String       = configuration.get[String]("urls.loginContinue")
  lazy val enrolmentKey: String           = configuration.get[String]("microservice.services.auth.enrolmentKey")
  lazy val enrolmentProxyUrl: String                   = configuration.get[Service]("microservice.services.enrolment-store-proxy").fullServiceUrl
  lazy val enrolmentManagementFrontendEnrolUrl: String = configuration.get[String]("urls.enrolmentManagementFrontendEnrolUrl")
  lazy val enrolmentIdentifierKey: String              = configuration.get[String]("keys.enrolmentIdentifierKey")

  lazy val manageTransitMovementsUrl: String               = configuration.get[String]("urls.manageTransitMovementsFrontend")
  lazy val manageTransitMovementsViewDeparturesUrl: String = s"$manageTransitMovementsUrl/test-only/view-departures"
  lazy val serviceUrl                                      = s"$manageTransitMovementsUrl/index"

  lazy val departureBaseUrl: String = configuration.get[Service]("microservice.services.departures").baseUrl
  lazy val departureUrl: String     = configuration.get[Service]("microservice.services.departures").fullServiceUrl

  lazy val timeoutSeconds: String   = configuration.get[String]("session.timeoutSeconds")
  lazy val countdownSeconds: String = configuration.get[String]("session.countdownSeconds")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)
}
