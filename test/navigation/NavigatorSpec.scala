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

import base.SpecBase
import config.FrontendAppConfig
import controllers.routes
import generators.Generators
import pages._
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import javax.inject.Inject

class NavigatorSpec @Inject()(appConfig : FrontendAppConfig) extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new Navigator
  val viewDepartures = s"${appConfig.manageTransitMovementsViewDeparturesUrl}"

  "Navigator" - {
      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator.nextPage(UnknownPage, answers)
              .mustBe(routes.IndexController.onPageLoad())

      }
    }
    "Must go from AddSecurityConsignorEoriPage to SecurityConsignorEoriPage when user selects yes" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(ConfirmCancellationPage, true)
            .success
            .value
          navigator
            .nextPage(ConfirmCancellationPage, updatedAnswers)
            .mustBe(viewDepartures)
      }
    }
    "Must go from AddSecurityConsignorEoriPage to declaration view when user selects no" in {
      forAll(arbitrary[UserAnswers]) {
        answers =>
          val updatedAnswers = answers
            .set(ConfirmCancellationPage, true)
            .success
            .value
          navigator
            .nextPage(ConfirmCancellationPage, updatedAnswers)
            .mustBe(manageTransitMovementsViewDeparturesUrl)
      }
    }
  }
}
