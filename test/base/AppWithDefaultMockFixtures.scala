/*
 * Copyright 2020 HM Revenue & Customs
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

package base

import controllers.actions.{DataRequiredAction, DataRequiredActionImpl, DataRetrievalAction, FakeDataRetrievalAction, FakeIdentifierAction, IdentifierAction}
import models.UserAnswers
import org.mockito.Mockito
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.SessionRepository
import uk.gov.hmrc.nunjucks.NunjucksRenderer

class AppWithDefaultMockFixtures {
  self: SpecBase with BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory =>

  override def beforeEach {
    Mockito.reset(
      mockRenderer,
      mockSessionRepository
    )
  }

  final val mockRenderer: NunjucksRenderer           = mock[NunjucksRenderer]
  final val mockSessionRepository: SessionRepository = mock[SessionRepository]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder(fakeApplicationUserAnswers)
      .build()

  /**
    * An overrideable hook that allows for a specified UserAnswers value to be provided to GuiceApplicationBuilder.
    *
    * @return Optional user answers that if defefined, will be passed to the GuiceApplicationBuilder
    */
  def fakeApplicationUserAnswers: Option[UserAnswers] = None

  /**
    * An overrideable hook that allows for the extension of the [[GuiceApplicationBuilder]] or
    * if overridden, can wipe way some of the assumption in this builder.
    *
    * @note This builder assumes that the user doesn't need actual instances of:
    *         - [[DataRequiredAction]]
    *         - [[IdentifierAction]]
    *         - [[NunjucksRenderer]]
    *         - [[SessionRepository]]
    *         - [[DataRetrievalAction]]
    *
    * @return Optional user answers that if defefined, will be passed to the GuiceApplicationBuilder
    */
  def guiceApplicationBuilder(userAnswers: Option[UserAnswers]): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[NunjucksRenderer].toInstance(mockRenderer),
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers))
      )
}
