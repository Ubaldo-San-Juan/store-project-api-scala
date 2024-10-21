package models

import play.api.libs.json.{Json, OFormat}

case class Product (
                     id: Long,
                     barcode: String,
                     name: String,
                     description: Option[String],
                     price: BigDecimal,
                     stock: Long,
                     categoryId: Long,
                     supplierId: Long
                   )

object Product{
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}
