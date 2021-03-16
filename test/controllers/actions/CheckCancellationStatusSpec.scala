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

package controllers.actions

import akka.stream.Materializer
import base.{MockNunjucksRendererApp, SpecBase}
import connectors.DepartureMovementConnector
import controllers.routes
import models.LocalReferenceNumber
import models.response.ResponseDeparture
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Call, DefaultActionBuilder}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class CheckCancellationStatusSpec extends SpecBase with NunjucksSupport with MockNunjucksRendererApp {

//  implicit lazy val materializer: Materializer = app.materializer
  implicit lazy val Action                     = app.injector.instanceOf(classOf[DefaultActionBuilder])

  private val mockDepartureResponse: ResponseDeparture = {
    ResponseDeparture(
      LocalReferenceNumber("lrn"),
      "DepartureSubmitted"
    )
  }

  "a check cancellation status action" - {

    "load the correct view for correct status" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))
      when(mockConnector.getDeparture(any())(any()))
        .thenReturn(Future.successful(Some(mockDepartureResponse)))


    }
  }

}
