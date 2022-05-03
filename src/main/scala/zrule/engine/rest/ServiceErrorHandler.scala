package zrule.engine
package rest

import cats.Applicative
import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.apache.commons.lang3.exception.ExceptionUtils
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl

trait ServiceError extends Throwable {
  def message: String
  def asBase: ServiceError = this
  def code: Int
}

object ServiceError {
  val unknownErrorCode = 5999
}

case class BadRequestError(message: String) extends ServiceError { val code: Int = BadRequestError.code }
object BadRequestError { val code: Int = 400 }

case class NotFoundError(message: String) extends ServiceError { val code: Int = NotFoundError.code }
object NotFoundError { val code: Int = 4006 }

case class InternalError(message: String) extends ServiceError { val code: Int = InternalError.code }
object InternalError { val code: Int = 5002 }

@JsonCodec
case class ErrorResponse(
  code: Int,
  message: String,
)

object ErrorResponse {
  def from(e: ServiceError): ErrorResponse = ErrorResponse(code = e.code, message = e.message)

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, ErrorResponse] =
    jsonEncoderOf[M, ErrorResponse]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, ErrorResponse] =
    jsonOf[M, ErrorResponse]

}

trait ServiceErrorHandler[M[_]] { self: Http4sDsl[M] with ApiImplicits[M] =>

  def handleErrors(t: Throwable)(implicit app: Applicative[M]): M[Response[M]] = t match {
    case e: org.http4s.InvalidMessageBodyFailure =>
      println(e)
      BadRequest(ErrorResponse.from(BadRequestError(e.getMessage)))
    case e: NotFoundError =>
      println(e)
      NotFound(ErrorResponse.from(e))
    case e: BadRequestError =>
      println(e)
      BadRequest(ErrorResponse.from(e))
    case e: InternalError =>
      println(e)
      InternalServerError(ErrorResponse.from(e))
    case e =>
      println(e)
      println(ExceptionUtils.getStackTrace(e))
      InternalServerError(ErrorResponse(ServiceError.unknownErrorCode, e.getMessage))
  }

}
