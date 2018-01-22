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

package controllers.action

import org.scalamock.matchers.MockParameter
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import uk.gov.hmrc.apigatewayexample.controllers.action.{AccountAccessControl, Authority, NinoNotFoundOnAccount}
import uk.gov.hmrc.auth.core.ConfidenceLevel.{L100, L200}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.syntax.retrieved._
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.{ForbiddenException, HeaderCarrier}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class AccountAccessControlSpec extends UnitSpec with MockFactory  with OneInstancePerTest{
  val testNino: Nino = Nino("CS700100A")

  val mockAuthConnector: AuthConnector = mock[AuthConnector]

  val grantAccessRetrievals: MockParameter[GrantAccessRetrieval] = nino and confidenceLevel

  val accessControl: AccountAccessControl = new AccountAccessControl{
    override def serviceConfidenceLevel = L200
    override def authConnector() = mockAuthConnector
  }

  type GrantAccessRetrieval = Retrieval[Option[String] ~ ConfidenceLevel]

  def authPredicate(nino: Nino): Predicate = {
    Enrolment("HMRC-NI", Seq(EnrolmentIdentifier("NINO", nino.nino)), "Activated", None) and CredentialStrength(CredentialStrength.strong)
  }

  implicit val hc = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  "grantAccess" should {
    "error with unauthorised when account has low CL" in {
      (mockAuthConnector.authorise(_: Predicate, _: GrantAccessRetrieval)(_: HeaderCarrier, _: ExecutionContext))
        .expects(EmptyPredicate, grantAccessRetrievals, *, *)
        .returning(Future.successful(Some(testNino.nino) and L100))

      intercept[ForbiddenException]{
       await(accessControl.grantAccess())
      }
    }

    "find NINO only account when CL is correct" in {
      (mockAuthConnector.authorise(_: Predicate, _: GrantAccessRetrieval)(_: HeaderCarrier, _: ExecutionContext))
        .expects(EmptyPredicate, grantAccessRetrievals, *, *)
        .returning(Future.successful(Some(testNino.nino) and L200))

      await(accessControl.grantAccess()) shouldBe Authority(testNino, L200)
    }

    "fail to return authority when no NINO exists" in {
      (mockAuthConnector.authorise(_: Predicate, _: GrantAccessRetrieval)(_: HeaderCarrier, _: ExecutionContext))
        .expects(EmptyPredicate, grantAccessRetrievals, *, *)
        .returning(Future.successful(None and L200))

      intercept[NinoNotFoundOnAccount]{
        await(accessControl.grantAccess())
      }
    }
  }
}
