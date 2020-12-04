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

package cancellation.cancellationModels

import cancellation.specBase._
import com.lucidchart.open.xtract.XmlReader
import generators.MessagesModelGenerators.{arbitraryDeclarationRequest, arbitraryTraderPrincipalWithEori}
import models.messages.trader.TraderPrincipalWithEori
import models.messages.{DeclarationRequest => SubmittedDeclarationRequest}
import org.scalacheck.Arbitrary.arbitrary
import xml.XMLWrites._

class DeclarationRequestSpec extends SpecBase {

  "must de-serialise xml to DeclarationRequest" in {

    forAll(arbitrary[SubmittedDeclarationRequest], arbitrary[TraderPrincipalWithEori]) {
      (dr, tpwe) =>
        val declarationRequest = dr.copy(traderPrincipal = tpwe)
        val xml                = declarationRequest.toXml

        val result = XmlReader.of[DeclarationRequest].read(xml)

        result.errors must be(empty)
        result.toOption must be(defined)
    }

  }

}
