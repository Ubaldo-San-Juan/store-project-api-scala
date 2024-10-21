package controllers


import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class HolaQrSof @Inject()(val controllerComponents: ControllerComponents) extends  BaseController{
  def saludo() = Action{implicit  request: Request[AnyContent] =>
    Ok("Hola QrSof")
  }
}
