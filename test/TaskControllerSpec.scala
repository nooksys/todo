import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.OneAppPerSuite

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.test._
import play.api.test.Helpers._
import reactivemongo.play.json._
import play.api.inject.bind

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.inject.guice.GuiceApplicationBuilder
import models._
import play.modules.reactivemongo.ReactiveMongoApi
import utils.TaskHelper
import scala.concurrent.Future
import org.mockito.ArgumentMatchers

/**
 * This this is not using mock TaskDao instead of the real one for prevent interacting with database
 * it just test controller logic only
 */
class TaskControllerSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  val taskDao = mock[TaskDao]

  val task = TaskHelper.createRandomTask.copy(isDone = false)

  implicit override lazy val app = new GuiceApplicationBuilder()
    .overrides(
      bind[ReactiveMongoApi].toInstance(mock[ReactiveMongoApi]), // prevent database connection pool to be create
      bind[TaskDao].toInstance(taskDao)).build()

  "TaskController" should {

    "get success" in withSetup {
      when(taskDao.find(anyString)).thenReturn(Future.successful(Some(task)))
    } {
      val result = route(app, FakeRequest(GET, "/api/tasks/fakeId")).get
      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(task)
    }

    "return NotFound with non exist get" in withSetup {
      when(taskDao.find(anyString)).thenReturn(Future.successful(None))
    } {
      val result = route(app, FakeRequest(GET, "/api/tasks/fakeId")).get
      status(result) mustBe NOT_FOUND
    }

    "save success" in withSetup {
      when(taskDao.save(any[Task])).thenReturn(Future.successful(()))
    } {
      val result = route(app, FakeRequest(POST, "/api/tasks")
        .withJsonBody(Json.obj("subject" -> "test", "detail" -> "detail"))).get
      status(result) mustBe OK
      val response = contentAsJson(result).as[JsObject]

      // verify method invoke with expected Task
      verify(taskDao).save(Task(
        (response \ "id").as[String],
        "test",
        "detail",
        false))
    }

    "update success" in withSetup {
      when(taskDao.update(any[Task])).thenReturn(Future.successful(true))
    } {
      val result = route(app, FakeRequest(PUT, "/api/tasks/expectedUpdateId")
        .withJsonBody(Json.obj("subject" -> "test", "detail" -> "detail"))).get
      status(result) mustBe OK

      // verify method invoke with expected delete id
      verify(taskDao).update(Task(
        "expectedUpdateId",
        "test",
        "detail",
        false))
    }

    "delete success" in withSetup {
      when(taskDao.delete(anyString)).thenReturn(Future.successful(true))
    } {
      val result = route(app, FakeRequest(DELETE, "/api/tasks/expectedDeleteId")).get
      status(result) mustBe OK

      // verify method invoke with expected delete id
      verify(taskDao).delete("expectedDeleteId")
    }

    // fake return items
    val returnItems = List.fill(10)(TaskHelper.createRandomTask)

    "list success" in withSetup {
      when(taskDao.findAll(anyInt, anyInt)).thenReturn(Future.successful(returnItems))
    } {
      val result = route(app, FakeRequest(GET, "/api/tasks")).get
      status(result) mustBe OK
      val response = contentAsJson(result)
      (response \ "pageNo").validate[Int] mustBe JsSuccess(1) // default pageNo is 1
      (response \ "pageSize").validate[Int] mustBe JsSuccess(10) // default pageSize is 10
      (response \ "items").validate[List[Task]] mustBe JsSuccess(returnItems) // check return items
    }

    /////////// TEST VALIDATION ////////////
    "reject save with empty subject or detail" in withSetup() {
      val result = route(app, FakeRequest(POST, "/api/tasks")
        .withJsonBody(Json.obj(
          "subject" -> "",
          "detail" -> ""))).get
      status(result) mustBe BAD_REQUEST
      val response = contentAsJson(result)
      // test error message must exists
      (response \ "error" \ "subject").toOption.isDefined mustBe true
      (response \ "error" \ "detail").toOption.isDefined mustBe true
    }
    
    "reject update with empty subject or detail" in withSetup() {
      val result = route(app, FakeRequest(PUT, "/api/tasks/fakeId")
        .withJsonBody(Json.obj(
          "subject" -> "",
          "detail" -> ""))).get
      status(result) mustBe BAD_REQUEST
      val response = contentAsJson(result)
      // test error message must exists
      (response \ "error" \ "subject").toOption.isDefined mustBe true
      (response \ "error" \ "detail").toOption.isDefined mustBe true
    }

  }

  def withSetup(setUpBlock: => Unit)(testBlock: => Unit) {
    reset(taskDao)

    setUpBlock

    testBlock
  }
}