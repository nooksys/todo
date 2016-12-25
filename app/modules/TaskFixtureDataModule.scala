package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import models.TaskDao
import javax.inject.{ Inject, Singleton }
import models.mongodbdaos.MongoDbTaskDao
import utils.TaskHelper

import play.api.libs.concurrent.Execution.Implicits.defaultContext;
import scala.concurrent.Future

/**
 * enable this module to generate test fixture data
 */
class TaskFixtureDataModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[TaskFixtureInitializer].asEagerSingleton()
  }
}

/**
 * create 200 record test fixture if database is empty
 */
class TaskFixtureInitializer @Inject() (taskDao: TaskDao) {

  taskDao.count.flatMap {
    case count if count == 0 => taskDao.bulkInsert(List.fill(200)(TaskHelper.createRandomTask))
    case _                   => Future.successful(())
  }

}