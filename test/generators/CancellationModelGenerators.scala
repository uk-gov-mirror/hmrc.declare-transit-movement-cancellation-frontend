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

package cancellation

import java.time.LocalDate

import models.{EoriNumber, LocalReferenceNumber, PrincipalAddress}
import cancellation.cancellationModels._
import generators.{Generators, JourneyModelGenerators}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import generators.MessagesModelGenerators._
import models.messages.Meta

object CancellationModelGenerators extends JourneyModelGenerators with Generators {

  implicit val arbitraryHeader: Arbitrary[Header] = Arbitrary {
    for {
      docNumHEA5        <- arbitrary[LocalReferenceNumber]
      datOfCanReqHEA147 <- arbitrary[LocalDate]
      canReaHEA250      <- alphaNumericWithMaxLength(22)

    } yield Header(docNumHEA5, datOfCanReqHEA147, canReaHEA250)
  }

  implicit val arbitraryPrincipalName: Arbitrary[PrincipalName] = Arbitrary {
    for {
      name <- alphaNumericWithMaxLength(stringMaxLength)
    } yield PrincipalName(name)
  }

  implicit val arbitraryPrincipalTrader: Arbitrary[PrincipalTrader] = Arbitrary {
    for {
      name    <- arbitrary[Option[PrincipalName]]
      address <- arbitrary[PrincipalAddress]
      tin     <- arbitrary[EoriNumber]
    } yield {
      val nameAndaddress = name.map(
        n => (n, address)
      )

      PrincipalTrader(nameAndaddress, tin)
    }

  }

  implicit val arbitraryCustomsOfficeDeparture: Arbitrary[CustomsOfficeDeparture] = Arbitrary {
    for {
      referenceNumber <- extendedAsciiWithMaxLength(stringMaxLength)
    } yield CustomsOfficeDeparture(referenceNumber)
  }

  implicit val arbitraryDeclarationCancellationRequest: Arbitrary[DeclarationCancellationRequest] =
    Arbitrary {
      for {
        meta                   <- arbitrary[Meta]
        header                 <- arbitrary[Header]
        principalTrader        <- arbitrary[PrincipalTrader]
        customsOfficeDeparture <- arbitrary[CustomsOfficeDeparture]
      } yield DeclarationCancellationRequest(meta, header, principalTrader, customsOfficeDeparture)
    }

}
