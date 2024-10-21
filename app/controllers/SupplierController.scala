package controllers

import models.Supplier
import models.daos.SupplierDAO
import play.api.mvc.{BaseController, ControllerComponents}
import play.api.libs.json._

import play.api.mvc._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SupplierController @Inject() (val controllerComponents: ControllerComponents, supplierDAO: SupplierDAO)
                                   (implicit ex: ExecutionContext) extends BaseController {

  def createSupplier = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").asOpt[String]
    val address = (request.body \ "address").asOpt[String]
    val email = (request.body \ "email").asOpt[String]
    val phoneNumber = (request.body \ "phoneNumber").asOpt[String]

    (name, address, email, phoneNumber) match {
      case (Some(name), Some(address), Some(email), Some(phoneNumber)) =>
        supplierDAO.createSupplier(name, address, email, phoneNumber).map { id =>
        Created(Json.obj("id" -> id, "Message" -> s"Supplier created with id $id"))
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid Data")))
    }
  }

  def getSuppliers() = Action.async { implicit request: Request[AnyContent] =>
    supplierDAO.getAllSuppliers().map { suppliers =>
      Ok(Json.toJson(suppliers))
    }
  }

  def getSupplier(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    supplierDAO.getSupplierById(id).map {
      case Some(supplier) => Ok(Json.toJson(supplier))
      case None => NotFound(Json.obj("Message" -> "Supplier not found"))
    }
  }

  def deleteSupplier(id: Long) = Action.async { implicit  request: Request[AnyContent] =>
    supplierDAO.deleteSupplier(id).map { result =>
      if (result > 0) {
        Ok(Json.obj("Message" -> s"Supplier with id $id deleted"))
      } else {
        NotFound(Json.obj("Message" -> s"Supplier not found"))
      }
    }
  }

  def updateSupplier(id: Long) = Action.async(parse.json) { implicit request =>
    val name = (request.body \ "name").asOpt[String]
    val address = (request.body \ "address").asOpt[String]
    val email = (request.body \ "email").asOpt[String]
    val phoneNumber = (request.body \ "phoneNumber").asOpt[String]

    (name, address, email, phoneNumber) match {
      case (Some(name), Some(address), Some(email), Some(phoneNumber)) =>
        val supplier = Supplier(id = id, name = name, address = address, email = email, phoneNumber = phoneNumber)

        supplierDAO.updateSupplier(id, supplier).map { result =>
          if (result > 0 ) {
            Ok(Json.obj("Message" -> s"Supplier with id $id updated"))
          } else {
            NotFound(Json.obj("Message" -> "Supplier not found"))
          }
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid Data")))
    }
  }
}
