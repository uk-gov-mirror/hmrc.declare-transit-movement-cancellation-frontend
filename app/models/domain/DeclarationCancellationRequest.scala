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

import cats.implicits._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.LocalReferenceNumber
import models.messages.Meta
import xml.XMLWrites
import xml.XMLWrites._

case class DeclarationCancellationRequest(
  meta: Meta,
  header: Header,
  principalTrader: PrincipalTrader,
  departureCustomsOffice: CustomsOfficeDeparture
)

object DeclarationCancellationRequest {

  implicit def writes: XMLWrites[DeclarationCancellationRequest] = XMLWrites[DeclarationCancellationRequest] {
    case DeclarationCancellationRequest(meta, header, principalTrader, departureCustomsOffice) =>
      <CC014B>
        {meta.toXml}
        {header.toXml}
        {principalTrader.toXml}
        {departureCustomsOffice.toXml}
      </CC014B>

  }

  implicit val reads: XmlReader[DeclarationCancellationRequest] = (
    __.read[Meta],
    (__ \ "HEAHEA").read[Header],
    (__ \ "TRAPRIPC1").read[PrincipalTrader],
    (__ \ "CUSOFFDEPEPT").read[CustomsOfficeDeparture]
  ).mapN(apply)

}
