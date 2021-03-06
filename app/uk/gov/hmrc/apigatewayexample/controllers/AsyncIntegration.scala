/*
 * Copyright 2018 HM Revenue & Customs
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

import akka.actor.{ActorRef, Props}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Controller, Request}
import uk.gov.hmrc.play.asyncmvc.async.{AsyncMVC, AsyncPaths}

case class AsyncResponse(value:JsValue)
object AsyncResponse {
  implicit val format = Json.format[AsyncResponse]
}

trait AsyncMvcIntegration extends AsyncMVC[AsyncResponse] {

  self:Controller =>

  val actorName = "async_api-gateway-actor"
  override def id = "async_api-gateway-id"

  override def asyncPaths(implicit request:Request[AnyContent]) = Seq(
    AsyncPaths(id, routes.LiveExampleAsyncController.poll(None).url)
  )

  override def outputToString(in:AsyncResponse): String = {
    implicit val format = Json.format[AsyncResponse]
    Json.stringify(Json.toJson(in))
  }

  override def convertToJSONType(in:String) : AsyncResponse = {
    val json=Json.parse(in)
    val response=json.asOpt[AsyncResponse]
    response.getOrElse(throw new Exception("Failed to build the response object?"))
  }

  override def waitForAsync = Call("GET","/poll")

  override def      throttleLimit = 300
  override def  blockingDelayTime = 3000

  final val CLIENT_TIMEOUT=5000L

  lazy val asyncActor: ActorRef = Akka.system.actorOf(Props(new AsyncMVCAsyncActor(taskCache, CLIENT_TIMEOUT)), actorName)
  override def         actorRef = asyncActor
  override def getClientTimeout = CLIENT_TIMEOUT

}
