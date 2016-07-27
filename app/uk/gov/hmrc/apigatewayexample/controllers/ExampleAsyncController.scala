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

package uk.gov.hmrc.apigatewayexample.controllers

import uk.gov.hmrc.msasync.repository.AsyncRepository
import uk.gov.hmrc.apigatewayexample.controllers.action.AccountAccessControlWithHeaderCheck
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.asyncmvc.async.{TimedEvent, Cache}
import uk.gov.hmrc.play.asyncmvc.model.{ViewCodes, TaskCache}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.api.controllers._

import scala.concurrent.{ExecutionContext, Future}


case class ExampleAsyncResponse(name:String, value:String)
object ExampleAsyncResponse {
  implicit val format = Json.format[ExampleAsyncResponse]
}

trait ExampleAsyncController extends BaseController with HeaderValidator with ErrorHandling with AsyncMvcIntegration {
  val accessControl: AccountAccessControlWithHeaderCheck
  val repository:AsyncRepository

  // This is the first call that is made to return a session cookie that will then be presented to async services.
  // The async services must have a secure key to identify the request before an async task is executed.
  final def start() = accessControl.validateAccept(acceptHeaderValidationRules).async {
    implicit authenticated =>
    Future.successful(Ok.withSession("AppKey" -> "Example"))
  }

  // Function wrapper verifies the session exists before proceeding to call an async function.
  private def withAsyncSession(func: => Future[Result])(implicit request:Request[AnyContent]) : Future[Result] = {
    request.session.get("AppKey").fold(Future.successful(BadRequest("Invalid request"))){ _ => func }
  }

  // The example async function. Before an async task can be executed, a session cookie is validated to enforce an identifier of the client
  // exists before an async task can be executed offline.
  final def exampleapi(testId:String, journeyId: Option[String] = None) = accessControl.validateAccept(acceptHeaderValidationRules).async {
    implicit authenticated =>
      implicit val hc = HeaderCarrier.fromHeadersAndSession(authenticated.request.headers, None)
      implicit val req = authenticated.request

      withAsyncSession {

        // Do not allow more than one task to be executing - if task running then poll page will be returned.
        asyncActionWrapper.async(callbackWithStatus) {
          flag =>

            // Async function wrapper responsible for executing below code onto a background queue.
            asyncWrapper(callbackWithStatus) {
              hc =>
                // Your code which returns a Future is place here!

                // Build a response with the value that was supplied to the action.
                val response = AsyncResponse(Json.toJson(ExampleAsyncResponse("some name", testId)))

                // NOTE: This is just an example to incur a delay before sending the response. This MUST not be copied into your code!
                TimedEvent.delayedSuccess(5000, 0).map { _ => response }
            }
        }
      }
  }

  def noTaskRunning = Action.async {
    implicit request =>
      Future.successful(BadRequest("No async task running!"))
  }

  /**
   * Remove the task from cache since sending reply to client.
   */
  private def removeTaskFromCache(id:Option[String])(func: => Future[Result])(implicit request:Request[AnyContent]) : Future[Result] = {
    id.fold(func){ taskId => repository.removeById(taskId).flatMap(_ => func)}
  }


  // Note:
  // The following functions callbackWithStatus, callbackWithStatus and poll are the integration functions for play-async library.

  /**
   *  Callback from async framework to generate the successful Result. The off-line has task completed.
   */
  def callbackWithSuccessResponse(response:AsyncResponse)(id:String)(implicit request:Request[AnyContent]) : Future[Result] = {
    removeTaskFromCache(Some(id)) {
      Future.successful(Ok(response.value))
    }
  }

  /**
   * Callback from async framework to process 'status'. Build a reply to the client.
   */
  def callbackWithStatus(status:Int)(id:Option[String])(implicit request:Request[AnyContent]) : Future[Result] = {
    val res = status match {
      case ViewCodes.Timeout => Ok("TIMEOUT")
      case ViewCodes.Polling => Ok("POLL WAIT - CALLBACK AND SEND THE URL TO CALL " + routes.LiveExampleAsyncController.poll(None).absoluteURL())
      case ViewCodes.ThrottleReached => Ok("THROTTLE EXCEEDED")
      case ViewCodes.Error | _ => Ok("ERROR")
    }
    Future.successful(res)
  }

  /**
   * Invoke the library poll function to determine the response to the client.
   */
  def poll(journeyId: Option[String] = None) = accessControl.validateAccept(acceptHeaderValidationRules).async {
    implicit authenticated =>

      withAsyncSession {
        implicit val hc = HeaderCarrier.fromHeadersAndSession(authenticated.request.headers, None)
        implicit val req = authenticated.request

        val response = pollTask(Call("GET", "/notaskrunning"), callbackWithSuccessResponse, callbackWithStatus)
        // Convert 303 response to 404. The 303 is generated when no task exists in the users session!
        response.map(resp => {
          resp.header.status match {
            case 303 => NotFound
            case _ => resp
          }
        })
      }
  }
}

object LiveExampleAsyncController extends ExampleAsyncController {
  override val accessControl = AccountAccessControlWithHeaderCheck
  override implicit val ec: ExecutionContext = ExecutionContext.global

  override lazy val repository:AsyncRepository = AsyncRepository()

  override def taskCache: Cache[TaskCache] = new Cache[TaskCache] {

    val expire = 900000L // Note: The expire time must be greater than the client timeout.

    override def put(id: String, value: TaskCache)(implicit hc: HeaderCarrier): Future[Unit] = {
      repository.save(value, expire).map(_ => ())
    }

    override def get(id: String)(implicit hc: HeaderCarrier): Future[Option[TaskCache]] = {
      repository.findByTaskId(id).map {
        case Some(found) => Some(found.task)
        case _ => None
      }
    }
  }

}


