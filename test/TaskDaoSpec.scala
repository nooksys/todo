import java.util.UUID

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import org.joda.time.DateTime
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.time.Span.convertDurationToSpan
import org.scalatestplus.play.OneAppPerSuite
import org.scalatestplus.play.PlaySpec

import javax.inject.Singleton
import models._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import reactivemongo.core.errors.DatabaseException
import reactivemongo.play.json.JsObjectDocumentWriter
import models.mongodbdaos.MongoDbTaskDao
import org.apache.commons.lang3.RandomStringUtils
import utils.TaskHelper
import play.modules.reactivemongo.ReactiveMongoApi
import models.mongodbdaos.MongoDbTaskDao

class TaskDaoSpec extends PlaySpec with OneAppPerSuite {

  val timeout = 10 seconds

  implicit val config = ScalaFutures.PatienceConfig(timeout)

  implicit override lazy val app = new GuiceApplicationBuilder()
    // change database for testing
    .configure(Map("mongodb.uri" -> "mongodb://localhost:27017/test-todo")).build()

  "TaskDao" should withDao(100) { dao =>

    val testTask: Task = TaskHelper.createRandomTask

    "save success" in {
      Await.ready(dao.save(testTask), timeout)
    }

    "find success" in {
      Await.result(dao.find(testTask.id), timeout) mustBe Some(testTask)
    }

    "update success" in {
      val update = testTask.copy(detail = "new fake detail")
      Await.result(dao.update(update), timeout) mustBe true

      Await.result(dao.find(testTask.id), timeout) mustBe Some(update)
    }

    "delete success" in {
      Await.result(dao.delete(testTask.id), timeout) mustBe true
      Await.result(dao.find(testTask.id), timeout) mustBe None
    }

    "count collectly" in {
      Await.result(dao.count, timeout) mustEqual 100
    }

    "findAll collectly" in {
      Await.result(dao.findAll(20, 0), timeout).size mustEqual 20
      Await.result(dao.findAll(10, 95), timeout).size mustEqual 5
    }
  }

  def withDao[T](initialDataNumber: Int)(block: TaskDao => T) = {
    val dao = new MongoDbTaskDao(app.injector.instanceOf[ReactiveMongoApi])
    val col = Await.result(dao.collectionFuture, timeout)
    // clear all test data
    Await.ready(col.remove(Json.obj()), timeout)

    // create test fixture data
    Await.ready(col.bulkInsert(false)(List.fill[col.ImplicitlyDocumentProducer](initialDataNumber)(TaskHelper.createRandomTask): _*), timeout)

    block(dao)
  }
}