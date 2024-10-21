package models

import play.api.libs.json._

case class Supplier (id: Long, name: String, address: String, email: String, phoneNumber: String)

object Supplier{
  implicit val supplierFormat: OFormat[Supplier] = Json.format[Supplier]
}
