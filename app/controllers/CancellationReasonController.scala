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
import forms.CancellationReasonFormProvider
import models.{DepartureId, LocalReferenceNumber, Mode}
import pages.CancellationReasonPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CancellationReasonController @Inject()(
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: CancellationReasonFormProvider,
  appConfig: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "cancellationReason.njk"

  def onPageLoad(departureId: DepartureId, mode: Mode): Action[AnyContent] = identify.async {
    implicit request =>
      val json = Json.obj(
        "form"        -> form,
        "departureId" -> departureId,
        "mode"        -> mode,
        "onSubmitUrl" -> controllers.routes.CancellationReasonController.onSubmit(departureId).url,
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] = identify.async {

    implicit request =>
      val cancellationSubmission = controllers.routes.CancellationSubmissionConfirmationController.onPageLoad(departureId)

      form
        .bindFromRequest()
        .fold(
          formWithErrors => {

            val json = Json.obj(
              "form"        -> formWithErrors,
              "departureId" -> departureId,
              "mode"        -> mode
            )

            renderer.render(template, json).map(BadRequest(_))

          },
          value => Future.successful(Redirect(cancellationSubmission))
          //TODO:CONVERT VALUE TO XML IS ON A SEPARATE TICKET
        )
  }
}
