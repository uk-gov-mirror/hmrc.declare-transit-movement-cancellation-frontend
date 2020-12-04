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

import cancellation.CancellationModelGenerators._
import cancellation.specBase.SpecBase
import com.lucidchart.open.xtract.XmlReader
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.StreamlinedXmlEquality
import xml.XMLWrites._

class DeclarationCancellationRequestSpec extends SpecBase with StreamlinedXmlEquality {

  "must serialize Header to xml" in {
    forAll(arbitrary[DeclarationCancellationRequest]) {
      case sut @ DeclarationCancellationRequest(meta, header, principalTrader, departureCustomsOffice) =>
        val expectedResult =
          <CC014B>
            {meta.toXml}
            {header.toXml}
            {principalTrader.toXml}
            {departureCustomsOffice.toXml}
          </CC014B>

        sut.toXml mustEqual expectedResult
    }

  }

  "must deserialize Xml to Header" in {
    forAll(arbitrary[DeclarationCancellationRequest]) {
      cancellationRequest =>
        val sut = cancellationRequest.toXml

        val result = XmlReader.of[DeclarationCancellationRequest].read(sut)

        result.errors must be(empty)
        result.toOption.value mustBe cancellationRequest
    }
  }

}
