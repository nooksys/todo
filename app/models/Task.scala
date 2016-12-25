package models

import play.api.libs.json.Json
import java.util.UUID
import scala.concurrent.Future

case class Task(id: String, subject: String, detail: String, isDone: Boolean)

object Task {

  implicit val format = Json.format[Task]
}

trait TaskDao {

  def findAll(limit: Int, offset: Int): Future[List[Task]]

  def find(id: String): Future[Option[Task]]

  def save(task: Task): Future[Unit]

  /**
   * return true if success update some task otherwise false
   */
  def update(task: Task): Future[Boolean]

  /**
   * return true if success delete some task otherwise false
   */
  def delete(id: String): Future[Boolean]

  def count: Future[Int]

  def bulkInsert(data: List[Task]): Future[Unit]
}