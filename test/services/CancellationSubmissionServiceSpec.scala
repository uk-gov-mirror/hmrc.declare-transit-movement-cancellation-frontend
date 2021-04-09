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

package services

import connectors.DepartureMovementConnector
import models.response.errors.{InvalidState, InvalidStatus}
import models.response.{Message, Messages}
import models.{DepartureId, EoriNumber, UserAnswers}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.scalatest.TryValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Millis, Span}
import org.scalatestplus.mockito.MockitoSugar
import pages.CancellationReasonPage
import play.api.test.Helpers
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CancellationSubmissionServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar with ScalaFutures with TryValues {

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(300, Millis))

  trait Setup {
    val mockDepartureConnector: DepartureMovementConnector = mock[DepartureMovementConnector]
    val date = LocalDate.now()
    lazy val service = new CancellationSubmissionService(mockDepartureConnector, () => date)
    val departureId: DepartureId = DepartureId(1)
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val mrnAllocatedMessage = <CC028B>
      <SynIdeMES1>SynIdeMES1</SynIdeMES1>
      <SynVerNumMES2>SynVerNumMES2</SynVerNumMES2>
      <MesSenMES3>MesSenMES3</MesSenMES3>
      <SenIdeCodQuaMES4>SenIdeCodQuaMES4</SenIdeCodQuaMES4>
      <MesRecMES6>MesRecMES6</MesRecMES6>
      <RecIdeCodQuaMES7>RecIdeCodQuaMES7</RecIdeCodQuaMES7>
      <DatOfPreMES9>DatOfPreMES9</DatOfPreMES9>
      <TimOfPreMES10>TimOfPreMES10</TimOfPreMES10>
      <IntConRefMES11>IntConRefMES11</IntConRefMES11>
      <RecRefMES12>RecRefMES12</RecRefMES12>
      <AppRefMES14>AppRefMES14</AppRefMES14>
      <AckReqMES16>AckReqMES16</AckReqMES16>
      <ComAgrIdMES17>ComAgrIdMES17</ComAgrIdMES17>
      <TesIndMES18>TesIndMES18</TesIndMES18>
      <MesIdeMES19>MesIdeMES19</MesIdeMES19>
      <MesTypMES20>MesTypMES20</MesTypMES20>
      <ComAccRefMES21>ComAccRefMES21</ComAccRefMES21>
      <FirAndLasTraMES23>FirAndLasTraMES23</FirAndLasTraMES23>
      <HEAHEA>
        <RefNumHEA4>lrn</RefNumHEA4>
        <DocNumHEA5>mrn</DocNumHEA5>
        <AccDatHEA158>12122020</AccDatHEA158>
      </HEAHEA>
      <TRAPRIPC1>
        <NamPC17>name</NamPC17>
        <StrAndNumPC122>street</StrAndNumPC122>
        <PosCodPC123>xx11xx</PosCodPC123>
        <CitPC124>city</CitPC124>
        <CouPC125>GB</CouPC125>
        <NADLNGPC>EN</NADLNGPC>
        <TINPC159>eori</TINPC159>
        <HITPC126>holder tir</HITPC126>
      </TRAPRIPC1>
      <CUSOFFDEPEPT>
        <RefNumEPT1>AB12345C</RefNumEPT1>
      </CUSOFFDEPEPT>
    </CC028B>

  }

  "CancellationSubmissionService" - {
    "submit the message if messages is obtained and userAnswers exists" in new Setup {
      val messages: Messages = Messages(
        Seq(
          Message("IE007", <CC007B></CC007B>),
          Message("IE028", mrnAllocatedMessage)
        )
      )

      val response = HttpResponse(Helpers.NO_CONTENT, "")

      when(mockDepartureConnector.getMessages(eqTo(departureId))(any())).thenReturn(Future.successful(Right(messages)))
      when(mockDepartureConnector.submitCancellation(eqTo(departureId), any())(any()))
        .thenReturn(Future.successful(Right(response)))

      val userAnswers: UserAnswers = UserAnswers(departureId, EoriNumber("12345")).set(CancellationReasonPage(departureId), "some value").success.value

      service.submitCancellation(userAnswers).futureValue mustBe Right(response)
    }

    "return an invalid state if submit cancellation sending to departures fails" in new Setup {
      val messages: Messages = Messages(
        Seq(
          Message("IE007", <CC007B></CC007B>),
          Message("IE028", mrnAllocatedMessage)
        )
      )

      when(mockDepartureConnector.getMessages(eqTo(departureId))(any())).thenReturn(Future.successful(Right(messages)))
      when(mockDepartureConnector.submitCancellation(eqTo(departureId), any())(any()))
        .thenReturn(Future.successful(Left(InvalidStatus(Helpers.INTERNAL_SERVER_ERROR))))

      val userAnswers: UserAnswers = UserAnswers(departureId, EoriNumber("12345")).set(CancellationReasonPage(departureId), "some value").success.value

      service.submitCancellation(userAnswers).futureValue mustBe Left(InvalidState)
    }

    "return an invalid state if no MRN allocated message is found" in new Setup {
      val messages: Messages = Messages(
        Seq(
          Message("IE007", <CC007B></CC007B>),
          Message("IE077", mrnAllocatedMessage)
        )
      )

      when(mockDepartureConnector.getMessages(eqTo(departureId))(any())).thenReturn(Future.successful(Right(messages)))

      val userAnswers: UserAnswers = UserAnswers(departureId, EoriNumber("12345")).set(CancellationReasonPage(departureId), "some value").success.value

      service.submitCancellation(userAnswers).futureValue mustBe Left(InvalidState)
    }

    "return an invalid state if get messages fails" in new Setup {
      when(mockDepartureConnector.getMessages(eqTo(departureId))(any())).thenReturn(Future.successful(Left(InvalidStatus(Helpers.INTERNAL_SERVER_ERROR))))

      val userAnswers: UserAnswers = UserAnswers(departureId, EoriNumber("12345")).set(CancellationReasonPage(departureId), "some value").success.value

      service.submitCancellation(userAnswers).futureValue mustBe Left(InvalidState)
    }

    "return an invalid state if no cancellation reason is found" in new Setup {
      val userAnswers: UserAnswers = UserAnswers(departureId, EoriNumber("12345"))
      service.submitCancellation(userAnswers).futureValue mustBe Left(InvalidState)
    }
  }
}
