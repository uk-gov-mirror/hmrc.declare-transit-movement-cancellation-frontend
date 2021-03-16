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

import base.SpecBase
import connectors.DepartureMovementConnector
import models.requests.IdentifierRequest
import models.response.ResponseDeparture
import models.{DepartureId, EoriNumber, LocalReferenceNumber}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import play.api.mvc.Results._
import play.api.mvc.{ActionBuilder, AnyContent, DefaultActionBuilder, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CancellationStatusActionSpec extends SpecBase with BeforeAndAfterEach {

  implicit lazy val Action: ActionBuilder[Request, AnyContent] = app.injector.instanceOf(classOf[DefaultActionBuilder])

  val mockConnector = mock[DepartureMovementConnector]

  override def beforeEach: Unit = {
    super.beforeEach
    Mockito.reset(mockConnector)
  }

  private def fakeOkResult[A]: A => Future[Result] =
    a => Future.successful(Ok("fake ok result value"))

  "a check cancellation status action" - {
    "will get a 200 and will load the correct page when the departure status is DepartureSubmitted" in {
      val mockDepartureResponse: ResponseDeparture = {
        ResponseDeparture(
          LocalReferenceNumber("lrn"),
          "DepartureSubmitted"
        )
      }

      when(mockConnector.getDeparture(any())(any())).thenReturn(Future.successful(Some(mockDepartureResponse)))

      val sut = (new CheckCancellationStatusProvider(mockConnector)(implicitly))(DepartureId(1))

      val testRequest = IdentifierRequest(FakeRequest(GET, "/"), EoriNumber("eori"))

      val result: Future[Result] = sut.invokeBlock(testRequest, fakeOkResult)

      status(result) mustEqual OK
      contentAsString(result) mustEqual "fake ok result value"
    }
  }

}
