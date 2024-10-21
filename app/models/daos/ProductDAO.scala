package models.daos

import models.Product
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                           (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Tabla de productos
  class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def barcode = column[String]("barcode", O.Unique)
    def name = column[String]("name")
    def description = column[Option[String]]("description")
    def price = column[BigDecimal]("price")
    def stock = column[Long]("stock")
    def categoryId = column[Long]("category_id")
    def supplierId = column[Long]("supplier_id")

    def * = (id, barcode, name, description, price, stock, categoryId, supplierId) <> ((Product.apply _).tupled, Product.unapply)
  }

  private val products = TableQuery[ProductsTable]

  def createProduct(
                     barcode: String,
                     name: String,
                     description: Option[String],
                     price: BigDecimal,
                     stock: Long,
                     categoryId: Long,
                     supplierId: Long): Future[Long] = {

    val product = Product(0, barcode, name, description, price, stock, categoryId, supplierId)
    db.run((products returning products.map(_.id)) += product)
  }

  def getAllProducts(): Future[Seq[Product]] = {
    db.run(products.result)
  }

  def getProducById(id: Long): Future[Option[Product]] = {
    db.run(products.filter(_.id === id).result.headOption)
  }

  def updateProduct(id: Long, product: Product): Future[Int] = {
    val query = products.filter(_.id === id).update(product)
    db.run(query)
  }

  def deleteProduct(id: Long): Future[Int] = {
    db.run(products.filter(_.id === id).delete)
  }

}
