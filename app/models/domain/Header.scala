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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.XMLReads._
import models.LocalReferenceNumber
import xml.XMLWrites
import xml.XMLValueWriter._

case class Header(
  docNumHEA5: LocalReferenceNumber, // Document/reference number
  datOfCanReqHEA147: LocalDate, // Date of cancellation request
  canReaHEA250: String // Cancellation reason
)

object Header {

  object Constants {
    val cancellationReason: Int = 350
  }

  implicit def writes: XMLWrites[Header] = XMLWrites[Header] {
    case Header(docNumHEA5, datOfCanReqHEA147, canReaHEA250) =>
      <HEAHEA>
        <DocNumHEA5>{docNumHEA5.asXmlText}</DocNumHEA5>
        <DatOfCanReqHEA147>{datOfCanReqHEA147.asXmlText}</DatOfCanReqHEA147>
        <CanReaHEA250>{canReaHEA250.asXmlText}</CanReaHEA250>
      </HEAHEA>

  }

  implicit val reads: XmlReader[Header] = (
    (__ \ "DocNumHEA5").read[String].map(LocalReferenceNumber.unsafeApply),
    (__ \ "DatOfCanReqHEA147").read[LocalDate],
    (__ \ "CanReaHEA250").read[String]
  ).mapN(apply)

}
