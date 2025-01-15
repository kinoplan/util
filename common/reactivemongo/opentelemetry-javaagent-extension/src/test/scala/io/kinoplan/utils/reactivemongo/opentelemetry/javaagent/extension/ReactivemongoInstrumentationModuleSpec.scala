package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension

import scala.concurrent.Future

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import reactivemongo.api.AsyncDriver
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.document

class ReactivemongoInstrumentationModuleSpec
    extends AsyncWordSpec
      with Matchers
      with BeforeAndAfterAll {

  var container: GenericContainer[Nothing] = _
  val driver: AsyncDriver = AsyncDriver()

  def tools: Future[BSONCollection] = driver
    .connect(s"mongodb://${container.getHost}:${container.getFirstMappedPort}")
    .flatMap(_.database("test"))
    .map(_.collection[BSONCollection]("tools"))

  override def beforeAll(): Unit = {
    super.beforeAll()

    val MONGODB_IMAGE = DockerImageName.parse("mongo:5.0")
    container = new GenericContainer(MONGODB_IMAGE).withExposedPorts(27017)
    container.start()
  }

  override def afterAll(): Unit = {
    container.stop()
    super.afterAll()
  }

  "Reactivemongo Instrumentation Module" should {
    "test run" in {
      // Thread.sleep(1000 * 30)
      for {
        t <- tools
        _ <- t.insert.one(document("name" -> "geralt"))
      } yield true shouldBe true
    }
  }

}
