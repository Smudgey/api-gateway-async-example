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

package controllers

import java.util.UUID.randomUUID

import akka.actor.{ActorRef, Props}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.JsValue
import play.api.test.FakeRequest
import play.api.test.Helpers._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.apigatewayexample.controllers.ExampleAsyncController
import uk.gov.hmrc.apigatewayexample.controllers.action.{AccountAccessControl, AccountAccessControlCheckAccessOff, AccountAccessControlWithHeaderCheck, Authority}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.mongo.{DatabaseUpdate, Saved}
import uk.gov.hmrc.msasync.repository.{AsyncRepository, TaskCachePersist}
import uk.gov.hmrc.play.asyncmvc.async.Cache
import uk.gov.hmrc.play.asyncmvc.model.TaskCache
import uk.gov.hmrc.play.auth.microservice.connectors.ConfidenceLevel.L200

import scala.concurrent.{ExecutionContext, Future}

class TestRepository extends AsyncRepository {
  override def save(task: TaskCache, expire:Long): Future[DatabaseUpdate[TaskCachePersist]] = Future.successful(DatabaseUpdate(null, Saved(TaskCachePersist(BSONObjectID.generate, task))))

  override  def findByTaskId(id: String): Future[Option[TaskCachePersist]] = ???

  override def removeById(id: String): Future[Unit] = Future.successful({})
}

class TestAccessCheck(nino: Option[Nino]) extends AccountAccessControl {
  override def grantAccess()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Authority] = Future(Authority(nino.get, L200))
}

class TestAccountAccessControlWithAccept(testAccessCheck:AccountAccessControl) extends AccountAccessControlWithHeaderCheck {
  override val accessControl: AccountAccessControl = testAccessCheck
}


trait Setup {
  implicit val hc = HeaderCarrier()
  val journeyId = Option(randomUUID().toString)

  val nino = Nino("CS700100A")
  val acceptHeader = "Accept" -> "application/vnd.hmrc.1.0+json"
  val emptyRequest = FakeRequest()

  val id = "someId"
  val task = TaskCache("someId", 1, Some("""{"value":1}"""), 1, 1)

  def fakeRequest(body:JsValue) = FakeRequest(POST, "url").withBody(body)
    .withHeaders("Content-Type" -> "application/json")

  val emptyRequestWithAcceptHeader = FakeRequest().withHeaders(acceptHeader)
  val requestWithSessionKeyAndAcceptHeader = FakeRequest().withSession(
    "AppKey" -> "SomeKey" // TODO...CONSTANT!
  ).withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")


  val testRepository = new TestRepository
  val testAccess = new TestAccessCheck(Some(nino))
  val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)
  val sandboxCompositeAction = AccountAccessControlCheckAccessOff
}

trait ControllerUnderTest extends ExampleAsyncController {
  def getName :String = ???
  override def waitMode = false
  override lazy val asyncActor: ActorRef = Akka.system.actorOf(Props(new AsyncMVCAsyncActor(taskCache, CLIENT_TIMEOUT)), name = getName)
}

class CacheStore extends Cache[TaskCache] {
  var item:Option[TaskCache] = None

  override def put(id: String, value: TaskCache)(implicit hc: HeaderCarrier): Future[Unit] = {
    item = Some(value)
    Future.successful(Unit)
  }

  override def get(id: String)(implicit hc: HeaderCarrier): Future[Option[TaskCache]] = {
    Future.successful(item)
  }
}

trait Success extends Setup {

  val controller = new ControllerUnderTest {
    val testSessionId="TestIdBlock"
    val testSessionIdIdentifier = s"${this.id}-${testSessionId}"
    val cache = new CacheStore()

    override def getName = "actor_"+testSessionId
    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
    override implicit val ec: ExecutionContext = ExecutionContext.global
    override def buildUniqueId() = testSessionId
    override def taskCache: Cache[TaskCache] = cache
    // TODO...
    override val repository: AsyncRepository = testRepository // TODO...
  }
}

trait Timeout extends Setup {

  val controller = new ControllerUnderTest {
    val testSessionId="TimeoutBlock"
    val testSessionIdIdentifier = s"${this.id}-${testSessionId}"
    val cache = new CacheStore()

    override def getName = "actor_"+testSessionId
    override def getClientTimeout = 1000
    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
    override implicit val ec: ExecutionContext = ExecutionContext.global
    override def buildUniqueId() = testSessionId
    override def taskCache: Cache[TaskCache] = cache
    override lazy val asyncActor: ActorRef = Akka.system.actorOf(Props(new AsyncMVCAsyncActor(taskCache, 1000)), name = getName)
    // TODO...
    override val repository: AsyncRepository = testRepository // TODO...
  }
}

trait Throttle extends Setup {

  val controller = new ControllerUnderTest {
    val testSessionId="TimeoutBlock"
    val testSessionIdIdentifier = s"${this.id}-${testSessionId}"
    override def throttleLimit=0
    override lazy val asyncActor: ActorRef = ???

    override def getName = testSessionId
    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
    override implicit val ec: ExecutionContext = ExecutionContext.global
    override def taskCache: Cache[TaskCache] = ???
    override def buildUniqueId() = testSessionId
    override def getClientTimeout = 80000
    // TODO..
    override val repository: AsyncRepository = testRepository
  }
}

trait SetupConcurrencyDynamicBlocking extends Setup {

  val testSessionId="TestIdConcurrency"
  implicit val req = requestWithSessionKeyAndAcceptHeader

  val controller  : ExampleAsyncController = new ControllerUnderTest {
    val cache = new CacheStore()

    override def getName = testSessionId
    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
    override implicit val ec: ExecutionContext = ExecutionContext.global
    override def taskCache: Cache[TaskCache] = cache
    override def buildUniqueId() = testSessionId
    override def getClientTimeout = 80000
    override lazy val asyncActor: ActorRef = Akka.system.actorOf(Props(new AsyncMVCAsyncActor(taskCache, getClientTimeout)), name = getName)
    // TODO..
    override val repository: AsyncRepository = testRepository

  }
}

trait AuthWithoutNino extends Setup {
  override val testAccess = new TestAccessCheck(None){
    override def grantAccess()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Authority] = Future.failed(new Upstream4xxResponse("Error", 401, 401))
  }

  override val testCompositeAction = new TestAccountAccessControlWithAccept(testAccess)

  val controller = new ExampleAsyncController {
    val cache = new CacheStore()

    override val accessControl: AccountAccessControlWithHeaderCheck = testCompositeAction
    override implicit val ec: ExecutionContext = ExecutionContext.global
    override def taskCache: Cache[TaskCache] = cache
    // TODO..
    override val repository: AsyncRepository = testRepository
  }
}
