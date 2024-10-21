package models

import play.api.libs.json._

case class Category(
                     id: Long,
                     name: String
                   )
object Category {
  implicit val userFormat: OFormat[Category] = Json.format[Category]
}