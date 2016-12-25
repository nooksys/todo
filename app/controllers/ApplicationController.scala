package controllers

import play.api.mvc.Controller
import play.api.Environment
import javax.inject.{ Inject, Singleton }
import play.api.mvc.Action

@Singleton
class ApplicationController @Inject() (env: Environment) extends Controller {

  /**
   * forward request to index.html that will handle all UI task
   */
  def index = Action {
    Ok.sendResource("public/index.html", env.classLoader)
  }
}