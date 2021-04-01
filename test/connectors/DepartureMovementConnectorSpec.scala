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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.Generators
import helper.WireMockServerHandler
import models.LocalReferenceNumber
import models.response.ResponseDeparture
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.Authorization

class DepartureMovementConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with Generators {
  implicit val hc: HeaderCarrier = HeaderCarrier(Some(Authorization("BearerToken")))

  private lazy val connector: DepartureMovementConnector =
    app.injector.instanceOf[DepartureMovementConnector]

  private val startUrl = "transits-movements-trader-at-departure"

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(conf = "microservice.services.departures.port" -> server.port())
    .build()

  private val departuresResponseJson = Json.obj(
    "referenceNumber" -> "lrn",
    "status"          -> "Submitted"
  )

  val errorResponses: Gen[Int] = Gen.chooseNum(400, 599)

  "DeparturesMovementConnector" - {
    "getDepartures" - {
      "must return a successful future response" in {
        val expectedResult = {
          ResponseDeparture(
            LocalReferenceNumber("lrn"),
            "Submitted"
          )
        }

        server.stubFor(
          get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}"))
            .withHeader("Channel", containing("web"))
            .willReturn(okJson(departuresResponseJson.toString()))
        )

        connector.getDeparture(departureId).futureValue mustBe Some(expectedResult)
      }

      "must return a None when an error response is returned from getDepartures" in {

        forAll(errorResponses) {
          errorResponse =>
            server.stubFor(
              get(urlEqualTo(s"/$startUrl/movements/departures/${departureId.index}"))
                .withHeader("Channel", containing("web"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponse)
                )
            )
            connector.getDeparture(departureId).futureValue mustBe None
        }
      }
    }

  }
}
