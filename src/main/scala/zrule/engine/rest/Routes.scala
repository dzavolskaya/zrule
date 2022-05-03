package zrule.engine
package rest

import cats.data.Kleisli
import cats.effect._
import cats.MonadError
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl._
import org.http4s.implicits._

import service.DecisionService

trait ApiImplicits[F[_]] {

  implicit def encoderJsonResponse[B: Encoder]: EntityEncoder[F, JsonResponse[B]] =
    jsonEncoderOf[F, JsonResponse[B]]

  implicit def decoderJsonResponse[B: Decoder](implicit sync: Sync[F]): EntityDecoder[F, JsonResponse[B]] =
    jsonOf[F, JsonResponse[B]]

  implicit def encoderErrorResponse: EntityEncoder[F, ErrorResponse] =
    jsonEncoderOf[F, ErrorResponse]

  implicit def decoderErrorResponse(implicit sync: Sync[F]): EntityDecoder[F, ErrorResponse] =
    jsonOf[F, ErrorResponse]

}

class Routes[M[_]: Sync](implicit M: MonadError[M, Throwable]) extends Http4sDsl[M] {
  def route(
    implicit clock: Clock[M],
    decisionService: DecisionService[M],
  ): Kleisli[M, Request[M], Response[M]] = {
    val decisionRouter = new DecisionRouter[M]()

    val routes = decisionRouter.decisionRoute

    routes.orNotFound
  }
}
