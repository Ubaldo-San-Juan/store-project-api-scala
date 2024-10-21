package models.daos

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                        (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Tabla de usuarios
  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")

    def * = (id, name, email) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def createUser(name: String, email: String): Future[Long] = {
    val user = User(0, name, email)
    db.run((users returning users.map(_.id)) += user)
  }

  // Obtener todos los usuarios
  def getAllUsers(): Future[Seq[User]] = {
    db.run(users.result)
  }

  // Obtener un usuario por ID
  def getUserById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  // Eliminar un usuario por ID
  def deleteUser(id: Long): Future[Int] = {
    db.run(users.filter(_.id === id).delete)
  }

  // Actualizar un usuario por ID
  def updateUser(id: Long, user: User): Future[Int] = {
    val query = users.filter(_.id === id).update(user)
    db.run(query)
  }

}
