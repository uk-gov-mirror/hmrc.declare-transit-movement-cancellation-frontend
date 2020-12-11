package controllers

import base.{MockNunjucksRendererApp, SpecBase}
import controllers.Assets.NO_CONTENT
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, times, verify, when}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, route, status, _}

import scala.concurrent.Future

class KeepAliveControllerSpec extends SpecBase with MockNunjucksRendererApp {
  "Keep alive controller" - {
    "touch mongo cache when lrn is available" in {
      when(mockSessionRepository.get(any(), any())).thenReturn(Future.successful(Some(emptyUserAnswers)))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      lazy val keepAliveRoute: String = routes.KeepAliveController.keepAlive(Some(lrn)).url
      val result                      = route(app, FakeRequest(GET, keepAliveRoute)).value

      status(result) mustBe NO_CONTENT
      verify(mockSessionRepository, times(1)).set(any())
      verify(mockSessionRepository, times(1)).get(any(), any())
    }

    "not touch mongo cache when lrn is not available" in {
      lazy val keepAliveRoute: String = routes.KeepAliveController.keepAlive(None).url
      val result                      = route(app, FakeRequest(GET, keepAliveRoute)).value

      status(result) mustBe NO_CONTENT
      verify(mockSessionRepository, never()).set(any())
      verify(mockSessionRepository, never()).get(any(), any())
    }

  }
}
