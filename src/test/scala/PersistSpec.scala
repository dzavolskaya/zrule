import java.time.LocalDateTime

import cats.effect.{Blocker, ContextShift, IO}
import doobie.scalatest.IOChecker
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.implicits._
import org.postgresql.PGProperty
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.funsuite.AnyFunSuite
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import zrule.engine.core.DecisionDeployment
import zrule.engine.persistence.DeploymentQuery._

class PersistSpec extends AnyFunSuite with Matchers with IOChecker with BeforeAndAfterAll {

  private var pg: EmbeddedPostgres = _
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  val dbPort = 5432
  val username = "postgres"
  val password = "postgres"

  val transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql:postgres",
    username,
    password,
    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )

  lazy val decision: DecisionDeployment = DecisionDeployment(
    id = 1,
    name = "test",
    tenant= "darya",
    version = 0,
    date = LocalDateTime.now(),
    code = "some text"
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    pg = EmbeddedPostgres.builder()
      .setPort(dbPort)
      .setConnectConfig(PGProperty.USER.getName, username)
      .setConnectConfig(PGProperty.PASSWORD.getName, password)
      .start()

    createTable.run.transact(transactor).unsafeRunSync() match {
      case _ => ()
    }
  }

  override def afterAll(): Unit = {
    super.afterAll()
    pg.close()
  }

  test("insert decision should succeed") { check(insert(decision)) }
  test("getById should succeed") { check(getById(id = 1)) }
  test("getByNameAndTenant should succeed") { check(getByNameAndTenant(name = "TestDecision", tenant = "darya", version = None)) }
  test("getLastVersionByNameAndTenant should succeed") { check(getLastVersionByNameAndTenant(name = "TestDecision", tenant = "darya")) }

}
