package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import org.scalactic.source.Position
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Collation, FailoverStrategy, ReadConcern, ReadPreference}
import reactivemongo.api.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  document
}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.collection.BSONSerializationPack.NarrowValueReader
import reactivemongo.api.indexes.Index

import io.kinoplan.utils.reactivemongo.base._

abstract class ReactiveMongoDaoBase[T](
  reactiveMongoApi: ReactiveMongoApi,
  collectionName: String,
  diagnostic: Boolean = true,
  autoCommentQueries: Boolean = true,
  failoverStrategyO: Option[FailoverStrategy] = None,
  readPreferenceO: Option[ReadPreference] = None
)(implicit
  ec: ExecutionContext
) extends BsonNoneAsNullProducer
      with BsonDocumentSyntax {

  protected object dao {

    def collection: Future[BSONCollection] = reactiveMongoApi
      .database
      .map(db =>
        failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _))
      )

    private def operations = ReactiveMongoOperations[T](collection)

    def smartEnsureIndexes(smartIndexes: Seq[SmartIndex], drop: Boolean = false)(implicit
      position: Position
    ): Future[Unit] = (
      for {
        coll <- collection
        _ <-
          if (drop) dropIndexes(coll, smartIndexes)
          else Future.successful(List.empty[Int])
        _ <- createIndexes(coll, smartIndexes)
      } yield ()
    ).withDiagnostic

    def count(
      selector: Option[BSONDocument] = None,
      limit: Option[Int] = None,
      skip: Int = 0,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = None
    )(implicit
      position: Position
    ): Future[Long] = collection
      .flatMap {
        Queries.countQ(_)(selector, limit, skip, readConcern, readPreference)
      }
      .withDiagnostic

    def countGrouped(
      groupBy: String,
      matchQuery: BSONDocument = document,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = None
    )(implicit
      position: Position
    ): Future[Map[String, Int]] = collection
      .flatMap {
        Queries.countGroupedQ(_)(groupBy, matchQuery, readConcern, readPreference)
      }
      .withDiagnostic

    def distinct[R](
      key: String,
      selector: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      collation: Option[Collation] = None
    )(implicit
      reader: NarrowValueReader[R],
      ec: ExecutionContext
    ): Future[Set[R]] = collection.flatMap {
      Queries.distinctQ[R](_)(key, selector, readConcern, collation)
    }

    def findAll(
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[T],
      position: Position,
      enclosing: sourcecode.Enclosing
    ): Future[List[T]] = findMany(readConcern = readConcern, readPreference = readPreference)

    def findMany[M <: T](
      selector: BSONDocument = document,
      projection: Option[BSONDocument] = None,
      sort: BSONDocument = document,
      hint: Option[BSONDocument] = None,
      skip: Int = 0,
      limit: Int = -1,
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[M],
      position: Position,
      enclosing: sourcecode.Enclosing
    ): Future[List[M]] = collection
      .flatMap {
        Queries.findManyQ(_)(
          selector,
          projection,
          sort,
          hint,
          skip,
          limit,
          readConcern,
          readPreference,
          withQueryComment
        )
      }
      .withDiagnostic

    def findManyByIds(
      ids: Set[BSONObjectID],
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[T],
      position: Position,
      enclosing: sourcecode.Enclosing
    ): Future[List[T]] = findMany(
      BSONDocument("_id" -> BSONDocument("$in" -> ids)),
      readConcern = readConcern,
      readPreference = readPreference
    )

    def findOne(
      selector: BSONDocument = BSONDocument(),
      projection: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO
    )(implicit
      r: BSONDocumentReader[T],
      position: Position,
      enclosing: sourcecode.Enclosing
    ): Future[Option[T]] = collection
      .flatMap {
        Queries.findOneQ(_)(selector, projection, readConcern, readPreference, withQueryComment)
      }
      .withDiagnostic

    def findOneById(
      id: BSONObjectID,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO
    )(implicit
      r: BSONDocumentReader[T],
      position: Position,
      enclosing: sourcecode.Enclosing
    ): Future[Option[T]] =
      findOne(BSONDocument("_id" -> id), readConcern = readConcern, readPreference = readPreference)

    def insertMany(values: List[T])(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ) = operations.insertMany(values).withDiagnostic

    def insertOne(value: T)(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ) = operations.insertOne(value).withDiagnostic

    def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(
      implicit
      position: Position
    ) = operations.update(q, u, multi = multi, upsert = upsert).withDiagnostic

    def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
      position: Position
    ) = operations.updateMany(values, f).withDiagnostic

    def upsert(q: BSONDocument, value: T)(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ) = operations.upsert(q, value).withDiagnostic

    def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ) = operations.saveOne(q, value, multi = multi, upsert = upsert).withDiagnostic

    def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ) = operations.saveMany(values, f).withDiagnostic

    def delete(q: BSONDocument)(implicit
      position: Position
    ) = operations.delete(q).withDiagnostic

    def deleteByIds(ids: Set[BSONObjectID])(implicit
      ec: ExecutionContext,
      position: Position
    ) = operations.deleteByIds(ids).withDiagnostic

    def deleteById(id: BSONObjectID)(implicit
      ec: ExecutionContext,
      position: Position
    ) = operations.deleteById(id).withDiagnostic

    private def withQueryComment(implicit
      enclosing: sourcecode.Enclosing
    ): Option[String] =
      if (autoCommentQueries) Some(QueryComment.make)
      else None

  }

  def findAll(
    readConcern: Option[ReadConcern] = None,
    readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
  )(implicit
    r: BSONDocumentReader[T],
    position: Position,
    enclosing: sourcecode.Enclosing
  ): Future[List[T]] = dao.findAll(readConcern, readPreference)

  def findManyByIds(
    ids: Set[BSONObjectID],
    readConcern: Option[ReadConcern] = None,
    readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
  )(implicit
    r: BSONDocumentReader[T],
    position: Position,
    enclosing: sourcecode.Enclosing
  ): Future[List[T]] = dao.findManyByIds(ids, readConcern, readPreference)

  def findOneById(
    id: BSONObjectID,
    readConcern: Option[ReadConcern] = None,
    readPreference: Option[ReadPreference] = readPreferenceO
  )(implicit
    r: BSONDocumentReader[T],
    position: Position,
    enclosing: sourcecode.Enclosing
  ): Future[Option[T]] = dao.findOneById(id, readConcern, readPreference)

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ) = dao.insertMany(values)

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ) = dao.insertOne(value)

  def deleteByIds(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext,
    position: Position
  ) = dao.deleteByIds(ids)

  def deleteById(id: BSONObjectID)(implicit
    ec: ExecutionContext,
    position: Position
  ) = dao.deleteById(id)

  private def createIndexes(
    coll: BSONCollection,
    smartIndexes: Seq[SmartIndex]
  ): Future[Seq[Boolean]] = Future.sequence(
    smartIndexes.map { smartIndex =>
      coll
        .indexesManager
        .ensure(
          Index(
            key = smartIndex.key.toSeq,
            name = smartIndex.name,
            unique = smartIndex.unique,
            background = smartIndex.background,
            partialFilter = smartIndex.partialFilter,
            expireAfterSeconds = smartIndex.expireAfterSeconds
          )
        )
    }
  )

  private def dropIndexes(coll: BSONCollection, smartIndexes: Seq[SmartIndex]): Future[List[Int]] =
    coll
      .indexesManager
      .list()
      .flatMap { indexes =>
        Future.sequence(
          indexes
            .filterNot(index =>
              smartIndexes.exists(smartIndex =>
                smartIndex.key == index.key.toSet &&
                ((smartIndex.name.nonEmpty && smartIndex.name.exists(index.name.contains(_))) ||
                smartIndex.name.isEmpty)
              ) || index.name.contains("_id_")
            )
            .flatMap(_.name)
            .map { indexName =>
              coll.indexesManager.drop(indexName)
            }
        )
      }

  implicit protected class FutureSyntax[A](future: Future[A]) {

    def withDiagnostic(implicit
      position: Position
    ): Future[A] =
      if (diagnostic) ensureDiagnostic
      else future

    def ensureDiagnostic(implicit
      position: Position
    ): Future[A] = future.recoverWith { case ex: Throwable =>
      val diagnosticInfo = s"At (${position.fileName}:${position.lineNumber})"
      if (ex.getMessage != null && ex.getMessage.startsWith(diagnosticInfo)) Future.failed(ex)
      else Future.failed(new Throwable(diagnosticInfo + " " + ex.getMessage, ex))
    }

  }

}
