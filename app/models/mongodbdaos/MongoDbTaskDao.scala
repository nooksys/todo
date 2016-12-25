package models.mongodbdaos

import models._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import play.api.libs.concurrent.Execution.Implicits.defaultContext;
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import play.api.libs.json._
import reactivemongo.play.json._
import reactivemongo.api.QueryOpts
import reactivemongo.api.Cursor
import javax.inject.Inject
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.Logger

@Singleton
class MongoDbTaskDao @Inject() (mongoApi: ReactiveMongoApi) extends TaskDao {

  def collectionFuture = mongoApi.database.map(_.collection[JSONCollection]("tasks"))

  def ensureIndex = collectionFuture.flatMap { col =>
    col.indexesManager.ensure(Index(Seq("id" -> IndexType.Ascending), unique = true))
  }

  ensureIndex

  def find(id: String) = collectionFuture.flatMap { col =>
    col.find(Json.obj("id" -> id)).one[Task]
  }

  def save(task: Task) = collectionFuture.flatMap { col =>
    col.insert(task).map(_ => ())
  }

  def update(task: Task) = collectionFuture.flatMap { col =>
    col.update(Json.obj("id" -> task.id), task).map(_.n > 0)
  }

  def delete(id: String) = collectionFuture.flatMap { col =>
    col.remove(Json.obj("id" -> id)).map(_.n > 0)
  }

  def count = collectionFuture.flatMap(_.count())

  def findAll(limit: Int, offset: Int) = collectionFuture.flatMap { col =>
    col.find(Json.obj()).options(QueryOpts().skip(offset)).cursor[Task]().collect[List](limit, err = Cursor.FailOnError[List[Task]]())
  }

  def bulkInsert(data: List[Task]) = collectionFuture.flatMap { col =>
    col.bulkInsert(false)(data.map(implicitly[col.ImplicitlyDocumentProducer](_)): _*).map(_ => ())
  }
}