package controllers

import play.api.mvc.Controller
import play.api.Environment
import javax.inject.{ Inject, Singleton }
import play.api.mvc.Action

@Singleton
class ApplicationController @Inject() (env: Environment) extends Controller {

  def index = Action {
    Ok.sendResource("public/index.html", env.classLoader)
  }
}