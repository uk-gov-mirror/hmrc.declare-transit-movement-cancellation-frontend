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

import utils.Format.dateFormatter

import scala.xml.Utility

trait XMLValueWriter[A] {
  def asXmlText(a: A): String
}

object XMLValueWriter {

  def apply[A](implicit ev: XMLValueWriter[A]): XMLValueWriter[A] = ev

  /** This implcit class allows for simple syntax to covert and object to a string representation
    * using the [[XMLValueWriter]] of the object. The string returned from the XMLValueWriter is
    * then escaped as per the XML spec. The escaping is implemented using [[scala.xml.Utility.escape]]
    *
    * @param a object to be serialized
    * @note This escapes the string automatically. If the string is escaped in the XMLValueWriter
    *       DO NOT USE THIS since this will result in double escaping, which this does not detect.
    */
  implicit class XMLValueWriterOps[A](val a: A) extends AnyVal {

    /** The extension method to convert the object to xml
      * @param ev the specific [[XMLValueWriter]] that will be used
      * @note This escapes the string automatically, if the string is escaped in the XMLValueWriter
      *       DO NOT USE THIS since this will result in double escaping, which this does not detect.
      */
    def asXmlText(implicit ev: XMLValueWriter[A]): String =
      a match {
        case _: String => ev.asXmlText(a)
        case _         => XMLValueWriter[String].asXmlText(ev.asXmlText(a))
      }

  }

  implicit val stringXmlValueWriter: XMLValueWriter[String] = string => Utility.escape(string)

  implicit val intXmlValueWriter: XMLValueWriter[Int] = int => int.toString

  implicit val dateFormattedXmlValueWriter: XMLValueWriter[LocalDate] = date => date.format(dateFormatter)

}
