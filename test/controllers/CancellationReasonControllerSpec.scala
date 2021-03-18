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

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.DepartureMovementConnector
import forms.CancellationReasonFormProvider
import matchers.JsonMatchers
import models.response.ResponseDeparture
import models.{LocalReferenceNumber, NormalMode}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.CancellationReasonPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport

import scala.concurrent.Future

class CancellationReasonControllerSpec extends SpecBase with MockNunjucksRendererApp with MockitoSugar with NunjucksSupport with JsonMatchers {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new CancellationReasonFormProvider()
  private val form         = formProvider()
  private val template     = "cancellationReason.njk"

  lazy val cancellationReasonRoute = routes.CancellationReasonController.onPageLoad(departureId).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]) toInstance (new FakeNavigator(onwardRoute)))

  "CancellationReason Controller" - {

    "must return OK and the correct view for a GET" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(GET, cancellationReasonRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> form,
        "mode"        -> NormalMode,
        "departureId" -> departureId
      )

      val jsonWithoutConfig = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual template
      jsonWithoutConfig must containJson(expectedJson)

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      val userAnswers = emptyUserAnswers.set(CancellationReasonPage, "answer").success.value
      dataRetrievalWithData(userAnswers)

      val request        = FakeRequest(GET, cancellationReasonRoute)
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val filledForm = form.bind(Map("value" -> "answer"))

      templateCaptor.getValue mustEqual template
    }

    "must redirect to the next page when valid data is submitted" in {

      checkCancellationStatus()

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      dataRetrievalWithData(emptyUserAnswers)

      val request =
        FakeRequest(POST, cancellationReasonRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual s"${controllers.routes.CancellationSubmissionConfirmationController.onPageLoad(departureId)}"

    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      checkCancellationStatus()

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request        = FakeRequest(POST, cancellationReasonRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm      = form.bind(Map("value" -> ""))
      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj(
        "form"        -> boundForm,
        "departureId" -> departureId,
        "mode"        -> NormalMode
      )

      templateCaptor.getValue mustEqual template
      jsonCaptor.getValue must containJson(expectedJson)

    }
  }
}
