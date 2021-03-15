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

package navigation

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import controllers.routes
import models._
import pages.{ConfirmCancellationPage, _}
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  protected def normalRoutes(appConfig:FrontendAppConfig): PartialFunction[Page, UserAnswers => Option[Call]] = {

    case ConfirmCancellationPage(departureId) => ua =>
      Some(confirmCancellationRoute(appConfig, ua,  departureId))
    case CancellationReasonPage(departureId) => ua =>  Some(routes.CancellationSubmissionConfirmationController.onPageLoad(departureId))
    case CancellationSubmissionConfirmationPage(departureId) => ???
    case _ => ???
  }

   def confirmCancellationRoute(appConfig:FrontendAppConfig,ua: UserAnswers, departureId: DepartureId): Call = {

     ua.get(ConfirmCancellationPage(departureId)) match {
       case Some(true) => routes.CancellationReasonController.onPageLoad(departureId)
       case Some(false) =>
       val viewDepartures = s"${appConfig.manageTransitMovementsViewDeparturesUrl}"
       Call("GET",viewDepartures)
    }
  }

  private def handleCall(userAnswers: UserAnswers, call: UserAnswers => Option[Call]) =
    call(userAnswers) match {
      case Some(onwardRoute) => onwardRoute
      case None => routes.SessionExpiredController.onPageLoad()
    }

  def nextPage(appConfig:FrontendAppConfig, page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(appConfig).lift(page) match {
        case None => routes.IndexController.onPageLoad()
        case Some(call) => handleCall(userAnswers, call)
      }
  }
}