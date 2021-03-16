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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import forms.ConfirmCancellationFormProvider
import models.{DepartureId, Mode}
import navigation.Navigator
import pages.ConfirmCancellationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmCancellationController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               identify: IdentifierAction,
                                               formProvider: ConfirmCancellationFormProvider,
                                               sessionRepository: SessionRepository,
                                               navigator:Navigator,
                                               getData: DataRetrievalActionProvider,
                                               requireData: DataRequiredAction,
                                               val controllerComponents: MessagesControllerComponents,
                                               renderer: Renderer
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "confirmCancellation.njk"

  def onPageLoad(departureId: DepartureId, mode: Mode): Action[AnyContent] = identify.async {
    implicit request =>

      val json = Json.obj(
        "form" -> form,
        "mode" -> mode,
        "departureId" -> departureId,
        "radios" -> Radios.yesNo(form("value")),
        "onSubmitUrl" -> controllers.routes.ConfirmCancellationController.onSubmit(departureId).url
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] = (identify andThen getData(departureId) andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {

          val json = Json.obj(
            "form" -> formWithErrors,
            "mode" -> mode,
            "departureId" -> departureId,
            "radios" -> Radios.yesNo(formWithErrors("value"))
          )
          renderer.render(template, json).map(BadRequest(_))
        },
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(ConfirmCancellationPage(departureId), value))
              _              <- sessionRepository.set(updatedAnswers)

            } yield Redirect(navigator.nextPage(ConfirmCancellationPage(departureId), mode, updatedAnswers))

      )
  }
}
