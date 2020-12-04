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

import cancellation.specBase.SpecBase
import com.lucidchart.open.xtract.XmlReader
import models.{EoriNumber, PrincipalAddress}
import org.scalacheck.Arbitrary.arbitrary
import xml.XMLWrites._
import cancellation.CancellationModelGenerators._
import org.scalatest.StreamlinedXmlEquality

import scala.xml.Utility.trim

class PrincipalTraderSpec extends SpecBase with StreamlinedXmlEquality {

  "must serialize to xml" - {
    "when name and address are present" in {
      val eoriNumber       = EoriNumber("TIN")
      val principalName    = PrincipalName("Name")
      val principalAddress = PrincipalAddress("Street and number", "City", "Postal code")
      val sut              = PrincipalTrader(Some((principalName, principalAddress)), eoriNumber)

      val expectedResult =
        <TRAPRIPC1>
          <NamPC17>Name</NamPC17>
          <StrAndNumPC122>Street and number</StrAndNumPC122>
          <PosCodPC123>Postal code</PosCodPC123>
          <CitPC124>City</CitPC124>
          <CouPC125>Country code</CouPC125>
          <TINPC159>TIN</TINPC159>
        </TRAPRIPC1>

      sut.toXml mustEqual expectedResult
    }

    "when only TIN (EORI) is present" in {
      val eoriNumber = EoriNumber("TIN")
      val sut        = PrincipalTrader(None, eoriNumber)

      val expectedResult =
        <TRAPRIPC1>
          <TINPC159>TIN</TINPC159>
        </TRAPRIPC1>

      sut.toXml mustEqual expectedResult
    }

  }

  "must deserialize from Xml" in {
    forAll(arbitrary[PrincipalTrader]) {
      principalTrader =>
        val sut = principalTrader.toXml

        val result = XmlReader.of[PrincipalTrader].read(sut)

        result.errors must be(empty)
        result.toOption.value mustBe principalTrader
    }
  }

}
