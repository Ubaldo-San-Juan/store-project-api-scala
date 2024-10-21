package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ ExecutionContext, Future }
import models.User
import models.daos.UserDAO

@Singleton
class UserController @Inject() (val controllerComponents: ControllerComponents, userDAO: UserDAO)
                               (implicit ec: ExecutionContext) extends BaseController {

  def createUser = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").asOpt[String]
    val email = (request.body \ "email").asOpt[String]

    (name, email) match {
      case (Some(name), Some(email)) =>
        userDAO.createUser(name, email).map { id =>
          Created(Json.obj("id" -> id, "Message" -> s"User created with id $id"))
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid Data")))
    }
  }

  def getUsers = Action.async { implicit request: Request[AnyContent] =>
    userDAO.getAllUsers().map { users =>
      Ok(Json.toJson(users))
    }
  }

  def getUser(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    userDAO.getUserById(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found"))
    }
  }

  def deleteUser(id: Long) = Action.async{ implicit  request: Request[AnyContent] =>
    userDAO.deleteUser(id).map { result =>
      if(result > 0){
        Ok(Json.obj("Message" -> s"User with id $id deleted"))
      } else {
        NotFound(Json.obj("Message" -> "User not found"))
      }
    }
  }

  def updateUser(id: Long) = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").asOpt[String]
    val email = (request.body \ "email").asOpt[String]

    (name, email) match {
      case (Some(name), Some(email)) =>
        val user = User(id = id, name = name, email = email)

        userDAO.updateUser(id, user).map { result =>
          if (result > 0) {
            Ok(Json.obj("Message" -> s"User with id $id updated"))
          } else {
            NotFound(Json.obj("Message" -> "User not found"))
          }
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid Data")))
    }
  }

}
