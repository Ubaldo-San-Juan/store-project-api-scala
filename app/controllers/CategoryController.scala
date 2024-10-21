package controllers

import models.Category
import models.daos.CategoryDAO
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.json._
import play.api.mvc._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject() (val controllerComponents: ControllerComponents, categoryDAO: CategoryDAO)
                                   (implicit ec: ExecutionContext) extends BaseController {

  def createCategory = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").asOpt[String]
    (name) match {
      case (Some(name)) =>
        categoryDAO.createCategory(name).map{ id =>
          Created(Json.obj("id" -> id, "Message" -> s"Category created with id $id"))
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid Data")))
    }
  }

  def getCategories = Action.async { implicit request: Request[AnyContent] =>
    categoryDAO.getAllCategories().map { categories =>
      Ok(Json.toJson(categories))
    }
  }

  def getCategory(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    categoryDAO.getCategoryById(id).map {
      case Some(category) => Ok(Json.toJson(category))
      case None => NotFound(Json.obj("Message: " -> "Category not found"))
    }
  }

  def deleteCategory(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    categoryDAO.deleteCategory(id).map { result =>
      if(result > 0) {
        Ok(Json.obj("Message" -> s"Category with id $id deleted"))
      } else {
        NotFound(Json.obj("Message" -> "Category not found"))
      }
    }
  }

  def updateCategory(id: Long) = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").asOpt[String]

    (name) match {
      case (Some(name)) =>
        val category = Category(id = id, name = name)

        categoryDAO.updateCategory(id, category).map { result =>
          if (result > 0){
            Ok(Json.obj("Message" -> s"Category with id $id updated"))
          } else {
            NotFound(Json.obj("Message" -> "Category not found"))
          }
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid Data")))
    }
  }
}
