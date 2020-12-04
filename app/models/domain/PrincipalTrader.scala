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

import cats.syntax.all._
import com.lucidchart.open.xtract.{__, XmlReader}
import models.{EoriNumber, PrincipalAddress}
import xml.XMLWrites
import xml.XMLWrites._
import xml.XMLValueWriter._

import scala.xml.NodeSeq

final case class PrincipalName(value: String)

object PrincipalName {

  implicit val writes: XMLWrites[PrincipalName] = {
    case PrincipalName(value) =>
      <NamPC17>{value.asXmlText}</NamPC17>
  }
}

final case class PrincipalTrader(
  nameAndaddress: Option[(PrincipalName, PrincipalAddress)],
  tin: EoriNumber // Eori
)

object PrincipalTrader {

  implicit val writes: XMLWrites[PrincipalTrader] = {
    implicit val principalAddressWrites: XMLWrites[PrincipalAddress] =
      XMLWrites[PrincipalAddress] {
        case PrincipalAddress(numberAndStreet, town, postcode) =>
          NodeSeq.fromSeq(
            Seq(
              <StrAndNumPC122>{numberAndStreet.asXmlText}</StrAndNumPC122>,
              <PosCodPC123>{postcode.asXmlText}</PosCodPC123>,
              <CitPC124>{town.asXmlText}</CitPC124>,
              <CouPC125>Country code</CouPC125>
            )
          )

      }

    implicit val nameAndAddressWrites =
      XMLWrites[(PrincipalName, PrincipalAddress)] {
        case (name, address) =>
          name.toXml ++ address.toXml
      }

    XMLWrites[PrincipalTrader] {
      case PrincipalTrader(nameAndaddress, tin) =>
        <TRAPRIPC1>
          {nameAndaddress.toXml}
          <TINPC159>{tin.asXmlText}</TINPC159>
        </TRAPRIPC1>

    }
  }

  implicit lazy val reads: XmlReader[PrincipalTrader] = (
    (__ \ "NamPC17").read[String].map(PrincipalName(_)).optional,
    (__ \ "StrAndNumPC122").read[String].optional,
    (__ \ "PosCodPC123").read[String].optional,
    (__ \ "CitPC124").read[String].optional,
    (__ \ "CouPC125").read[String].optional,
    (__ \ "TINPC159").read[String].map(EoriNumber(_))
  ).mapN {
    case (Some(name), Some(strAndNumPC122), Some(posCodPC123), Some(citPC124), _, eori) =>
      PrincipalTrader(Some((name, PrincipalAddress(strAndNumPC122, citPC124, posCodPC123))), eori)
    case (_, _, _, _, _, eori) =>
      PrincipalTrader(None, eori)

  }

}
