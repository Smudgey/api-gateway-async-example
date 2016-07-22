/*
 * Copyright 2016 HM Revenue & Customs
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

import org.scalatest.concurrent.PatienceConfiguration.{Interval, Timeout}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Milliseconds, Seconds, Span}
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.{FakeRequest, FakeApplication}
import uk.gov.hmrc.apigatewayexample.controllers.{LiveExampleAsyncController, ExampleAsyncResponse, ExampleAsyncController}
import uk.gov.hmrc.play.asyncmvc.async.TimedEvent
import uk.gov.hmrc.play.asyncmvc.model.AsyncMvcSession
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


class ExampleAsyncResponseSpec extends UnitSpec with WithFakeApplication with StubApplicationConfiguration with ScalaFutures with Eventually {

  override lazy val fakeApplication = FakeApplication(additionalConfiguration = config)

  "in async non-blocking mode, disconnect the HTTP client from the server side request and poll for the off-line task to complete " should {

    "submit a request and poll for a successful response" in new Success {

      invokeTestNonBlockAction(
        controller,
        controller.testSessionIdIdentifier,
        Json.stringify(Json.toJson(ExampleAsyncResponse("some name", controller.testSessionIdIdentifier))))(requestWithSessionKeyAndAcceptHeader)
    }

    "return timeout when the async task takes longer than the client will wait" in new Timeout {

      invokeTestNonBlockAction(
        controller,
        controller.testSessionIdIdentifier,
        "TIMEOUT")(requestWithSessionKeyAndAcceptHeader)
    }

    "return throttle status when the throttle limit has been reached" in new Throttle {
      val result2: Result = await(controller.exampleapi("id")(requestWithSessionKeyAndAcceptHeader))
      status(result2) shouldBe 200
      bodyOf(result2) shouldBe "THROTTLE EXCEEDED"
    }

    "poll request with an invalid request will return a 404 response" in new Success {

      val result2 = await(controller.poll()(requestWithSessionKeyAndAcceptHeader))
      status(result2) shouldBe 404
    }

    "poll request with no session will return a 400 response" in new Success {

      val result2 = await(controller.poll()(emptyRequestWithAcceptHeader))
      status(result2) shouldBe 400
    }

    "poll request with no Accept-Header will return 406" in new Success {

      val result2 = await(controller.poll()(emptyRequest))
      status(result2) shouldBe 406
    }


    "return 401 result when authority record does not contain a NINO" in new AuthWithoutNino {
      val result = await(controller.poll()(requestWithSessionKeyAndAcceptHeader))

      status(result) shouldBe 401
    }

  }

  "Simulating concurrent http requests through the async framework " should {

    "successfully process all concurrent requests and once all tasks are complete, verify the throttle value is 0" in {
      val time=System.currentTimeMillis()

      val concurrentRequests = (0 until 15).foldLeft(Seq.empty[SetupConcurrencyDynamicBlocking]) {
        (list, counter) => {
          val asyncRequest = new SetupConcurrencyDynamicBlocking {
            override val testSessionId=s"TestIdConcurrent$counter"
          }
          list ++ Seq(asyncRequest)
        }
      }

      val result = concurrentRequests.map { asyncTestRequest =>

        val delay = scala.util.Random.nextInt(50)
        TimedEvent.delayedSuccess(delay, 0).map(_ => {
          implicit val reqImpl = asyncTestRequest.req
          val ident = s"${LiveExampleAsyncController.id}-${asyncTestRequest.testSessionId}"

          val responseCheck = Json.stringify(Json.toJson(ExampleAsyncResponse("some name", ident)))

          invokeTestNonBlockAction(asyncTestRequest.controller, ident, responseCheck)
        })
      }

      eventually(Timeout(Span(95000, Milliseconds)), Interval(Span(2, Seconds))) {
        await(Future.sequence(result))
      }

      uk.gov.hmrc.play.asyncmvc.async.Throttle.current shouldBe 0

      println("Time spent processing... " + (System.currentTimeMillis()-time))
    }

  }

  def invokeTestNonBlockAction(controller:ExampleAsyncController, testSessionId:String, response:String)(implicit request:Request[AnyContent]) = {
    // Session object holds the key of the task being executed which is used in the poll request.
    val requestWithSessionKeyAndId = FakeRequest().withSession(
      controller.AsyncMVCSessionId -> controller.buildSession(controller.id,testSessionId),
      "AppKey" -> "SomeKey"
    ).withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")

    val result2 = await(controller.exampleapi(testSessionId, None)(request))
    status(result2) shouldBe 200

    val session = result2.session.get(controller.AsyncMVCSessionId)
    val jsonSession = Json.parse(session.get).as[AsyncMvcSession]
    jsonSession.id shouldBe testSessionId

    eventually(Timeout(Span(7, Seconds))) {
      val result2: Result = await(controller.poll()(requestWithSessionKeyAndId))
      status(result2) shouldBe 200
      bodyOf(result2) shouldBe response
    }
  }

}
