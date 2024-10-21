package models.daos

import models.Supplier
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SupplierDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                            (implicit executionContext: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  //Tabla de suppliers
  class SuppliersTable(tag: Tag) extends Table[Supplier](tag, "suppliers"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def address = column[String]("address")
    def email = column[String]("email")
    def phoneNumber = column[String]("phone_number")

    def * = (id, name, address, email, phoneNumber) <> ((Supplier.apply _).tupled, Supplier.unapply)
  }

  private val suppliers = TableQuery[SuppliersTable]

  def createSupplier(name: String, address: String, email: String, phoneNumber: String): Future[Long] = {
    val supplier = Supplier(0, name, address, email, phoneNumber)
    db.run((suppliers returning suppliers.map(_.id)) += supplier)
  }

  def getAllSuppliers(): Future[Seq[Supplier]] = {
    db.run(suppliers.result)
  }

  def getSupplierById(id: Long): Future[Option[Supplier]] = {
    db.run(suppliers.filter(_.id === id).result.headOption)
  }

  def updateSupplier(id: Long, supplier: Supplier): Future[Int] = {
    val query = suppliers.filter(_.id === id).update(supplier)
    db.run(query)
  }

  def deleteSupplier(id: Long): Future[Int] = {
    db.run(suppliers.filter(_.id === id).delete)
  }
}
