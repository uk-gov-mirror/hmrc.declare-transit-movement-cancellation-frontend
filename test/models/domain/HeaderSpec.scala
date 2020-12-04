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

import java.time.LocalDate

import com.lucidchart.open.xtract.XmlReader
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, StreamlinedXmlEquality}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import xml.XMLWrites._
import cancellation.CancellationModelGenerators._
import cancellation.specBase.SpecBase
import models.LocalReferenceNumber

import scala.xml.Utility.trim

class HeaderSpec extends SpecBase with StreamlinedXmlEquality {

  "must serialize Header to xml" in {
    val date   = LocalDate.of(2020, 12, 31)
    val header = Header(LocalReferenceNumber("1").get, date, "test")

    val expectedResult =
      <HEAHEA>
        <DocNumHEA5>1</DocNumHEA5>
        <DatOfCanReqHEA147>20201231</DatOfCanReqHEA147>
        <CanReaHEA250>test</CanReaHEA250>
      </HEAHEA>

    header.toXml mustEqual expectedResult
  }

  "must deserialize Xml to Header" in {
    forAll(arbitrary[Header]) {
      header =>
        val sut = header.toXml

        val result = XmlReader.of[Header].read(sut)

        result.errors must be(empty)
        result.toOption.value mustBe header
    }
  }

}
