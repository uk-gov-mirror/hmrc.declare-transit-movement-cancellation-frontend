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

package xml

import java.time.LocalDate

import base.SpecBase
import xml.XMLValueWriter._

class XMLValueWriterSpec extends SpecBase {

  "formats a string" - {
    "when there is escaping" in {
      val sut  = "1 > 2"
      val sut2 = "1 &>< 2"

      sut.asXmlText mustEqual "1 &gt; 2"
      sut2.asXmlText mustEqual "1 &amp;&gt;&lt; 2"
    }

    "when there is no escaping" in {
      val sut = "testString"

      sut.asXmlText mustEqual sut
    }
  }

  "formats an Int as a string" in {
    1.asXmlText mustEqual "1"
  }

  "formats an object using the XMLValueWriter and turns it into it's string representation" - {
    case class IntWord(a: String)
    case class IntWordMapper(x: Int, y: IntWord)

    implicit val xMLValueWriterIntWord: XMLValueWriter[IntWord] =
      o => o.a.asXmlText

    "when there is escaping" in {
      implicit val xMLValueWriterIntWordMapper: XMLValueWriter[IntWordMapper] =
        o => o.x.toString.asXmlText + " < 2 & in words is " + o.y.asXmlText

      val sut = IntWordMapper(1, IntWord("one"))

      sut.asXmlText mustEqual "1 &lt; 2 &amp; in words is one"
    }

    "when there is no escaping" in {

      implicit val xMLValueWriterIntWordMapper: XMLValueWriter[IntWordMapper] =
        o => o.x.toString.asXmlText + " in words is " + o.y.asXmlText

      val sut = IntWordMapper(1, IntWord("one"))

      sut.asXmlText mustEqual "1 in words is one"
    }

  }

  "formats a LocalDate as a string in format yyyyMMdd" in {
    val sut = LocalDate.of(2020, 12, 31)

    sut.asXmlText mustEqual "20201231"
  }

}
