package zrule.engine

import scala.util.Try

import cats.data.Kleisli
import cats.effect.{Clock, ExitCode, IOApp}
import doobie.Transactor
import monix.execution.Scheduler
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{Request, Response}

import service._
import persistence.Database
import rest.Routes

object ZruleApp extends IOApp {

  type M[X] = cats.effect.IO[X]
  val scheduler: Scheduler = monix.execution.Scheduler.global
  val databaseConfig: Try[Database] = Database.load()
  implicit val transactor: Transactor[M] = databaseConfig.map(_.transactor[M]).get

  implicit val decisionService: DecisionService[M] = new service.DecisionService[M]
  implicit val clock: Clock[M] = Clock.extractFromTimer[M]

  val application: Kleisli[M, Request[M], Response[M]] = new Routes[M]().route

  def run(args: List[String]): M[ExitCode] =
    BlazeServerBuilder[M](scheduler)
      .bindHttp(8080, "localhost")
      .withHttpApp(application)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
