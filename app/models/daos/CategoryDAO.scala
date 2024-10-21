package models.daos

import models.Category
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                            (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Tabla de usuarios
  class CategoriesTable(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  private val categories = TableQuery[CategoriesTable]

  // crear una categoria
  def createCategory(name: String): Future[Long] = {
    val category = Category(0, name)
    db.run((categories returning categories.map(_.id)) += category)
  }

  //Obtener una categoria por ID
  def getAllCategories(): Future[Seq[Category]] = {
    db.run(categories.result)
  }

  //Obtener una categoria por ID
  def getCategoryById(id: Long): Future[Option[Category]] = {
    db.run(categories.filter(_.id === id).result.headOption)
  }

  //Eliminar un usuario por ID
  def deleteCategory(id: Long): Future[Int] = {
    db.run(categories.filter(_.id === id).delete)
  }

  def updateCategory(id: Long, category: Category): Future[Int] = {
    val query = categories.filter(_.id === id).update(category)
    db.run(query)
  }
}
