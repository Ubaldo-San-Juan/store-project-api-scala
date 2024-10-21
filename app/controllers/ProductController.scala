package controllers

import javax.inject._
import models.Product
import models.daos.ProductDAO
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductController @Inject() (val controllerComponents: ControllerComponents, productDAO: ProductDAO)
                               (implicit ec: ExecutionContext) extends BaseController {

  def getProducts = Action.async { implicit request: Request[AnyContent] =>
    productDAO.getAllProducts().map { products =>
      Ok(Json.toJson(products))
    }
  }

  def getProduct(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    productDAO.getProducById(id).map {
      case Some(product) => Ok(Json.toJson(product))
      case None => NotFound(Json.obj("Message" -> s"Product with id $id not found"))
    }
  }

  import org.postgresql.util.PSQLException
  import scala.util.{Failure, Success, Try}

  def createProduct = Action.async(parse.json) { implicit request =>
    val barcode = (request.body \ "barcode").asOpt[String]
    val name = (request.body \ "name").asOpt[String]
    val description = (request.body \ "description").asOpt[String]
    val price = (request.body \ "price").asOpt[BigDecimal]
    val stock = (request.body \ "stock").asOpt[Long]
    val categoryId = (request.body \ "categoryId").asOpt[Long]
    val supplierId = (request.body \ "supplierId").asOpt[Long]

    (barcode, name, price, stock, categoryId, supplierId) match {
      case (Some(barcode), Some(name), Some(price), Some(stock), Some(categoryId), Some(supplierId)) =>
        Try {
          productDAO.createProduct(barcode, name, description, price, stock, categoryId, supplierId)
        } match {
          case Success(future) =>
            future.map { id =>
              Created(Json.obj("id" -> id, "Message" -> s"Product created with id $id"))
            }.recover {
              case ex: PSQLException if ex.getSQLState == "23505" => // Código SQL de error de unicidad
                Conflict(Json.obj("Message" -> "Duplicate barcode, product already exists"))
            }
          case Failure(ex) =>
            Future.successful(InternalServerError(Json.obj("Message" -> "Unexpected error occurred")))
        }

      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid data")))
    }
  }


  def updateProduct(id: Long) = Action.async(parse.json) { implicit request =>
    val barcode = (request.body \ "barcode").asOpt[String]
    val name = (request.body \ "name").asOpt[String]
    val description = (request.body \ "description").asOpt[String]
    val price = (request.body \ "price").asOpt[BigDecimal]
    val stock = (request.body \ "stock").asOpt[Long]
    val categoryId = (request.body \ "categoryId").asOpt[Long]
    val supplierId = (request.body \ "supplierId").asOpt[Long]

    (barcode, name, price, stock, categoryId, supplierId) match {
      case (Some(barcode), Some(name), Some(price), Some(stock), Some(categoryId), Some(supplierId)) =>
        val updateProduct = Product(id, barcode, name, description, price, stock, categoryId, supplierId)

        productDAO.updateProduct(id, updateProduct).map { result =>
          if (result > 0) {
            Ok(Json.obj("Message" -> s"Product with id $id updated"))
          } else {
            NotFound(Json.obj("Message" -> "Product not found"))
          }
        }
      case _ =>
        Future.successful(BadRequest(Json.obj("Message" -> "Invalid data")))
    }
  }

  def deleteProduct(id: Long) = Action.async { implicit  request: Request[AnyContent] =>
    productDAO.deleteProduct(id).map { result =>
      if(result > 0){
        Ok(Json.obj("Message" -> s"Product with id $id deleted"))
      }else {
        NotFound(Json.obj("Message" -> "Product not found"))
      }
    }

  }
}