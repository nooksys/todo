package controllers

import play.api.mvc.Controller
import models.TaskDao
import javax.inject.{ Inject, Singleton }
import play.api.mvc.Action
import play.api.libs.json._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext;
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import scala.concurrent.Future
import java.util.UUID
import models._

object TaskController {

  case class TaskData(subject: String, detail: String, isDone:Option[Boolean])

  implicit val taskDataFormat = Json.format[TaskData]

  val taskForm = Form(
    mapping(
      "subject" -> nonEmptyText,
      "detail" -> nonEmptyText,
      "isDone" -> optional(boolean))(TaskData.apply)(TaskData.unapply))
}

@Singleton
class TaskController @Inject() (taskDao: TaskDao)(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  import TaskController._

  /**
   * @return example { "id": "test", "subject": "test", "isDone": false }
   */
  def get(id: String) = Action.async {
    taskDao.find(id).map {
      case Some(task) => Ok(Json.toJson(task))
      case _          => NotFound
    }
  }

  /**
   * @return example { "id": "new generated id" } or { "error" { "{fieldName}": [mesages...] } }
   */
  def save = Action.async(parse.tolerantJson) { implicit request =>
    taskForm.bindFromRequest().fold(
      formWithErrors => Future.successful(errorResponse(formWithErrors.errorsAsJson)),
      data => {
        val id = UUID.randomUUID().toString
        taskDao.save(Task(id, data.subject, data.detail, false)).map(_ => Ok(Json.obj("id" -> id)))
      })
  }

  /**
   * @return NotFound with unavailable task
   */
  def update(id: String) = Action.async(parse.tolerantJson) { implicit request =>
    taskForm.bindFromRequest().fold(
      formWithErrors => Future.successful(errorResponse(formWithErrors.errorsAsJson)),
      data => taskDao.update(Task(id, data.subject, data.detail, data.isDone.getOrElse(false))).map {
        case success if success => Ok
        case _                  => NotFound
      })
  }

  /**
   * @return NotFound with unavailable task
   */
  def delete(id: String) = Action.async { implicit request =>
    taskDao.delete(id).map {
      case success if success => Ok
      case _                  => NotFound
    }
  }
  
  /**
   * @return example:
   * {
   * 	pageNo:1,
   *  pageSize: 10,
   *  totalRecords: 200,
   *  items: [
   *  	{id:"id", subject:"subject", "detail": "detail", isDone:true},
   *  	...
   *  	...
   *  ]
   * }
   */
  def list(pageNo: Int, pageSize: Int) = Action.async {
    taskDao.count.flatMap { count =>
      val page = if (pageNo > 0) pageNo else 1
      val limit = if (pageSize > 0) pageSize else 10
      val offset = (page - 1) * limit
      taskDao.findAll(limit, offset).map(items => Ok(Json.obj("pageNo" -> page, "pageSize" -> limit, "totalRecords" -> count, "items" -> items)))
    }
  }

  def errorResponse(errors: JsValue) = BadRequest(Json.obj("error" -> errors))
}