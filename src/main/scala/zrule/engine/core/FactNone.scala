package zrule.engine
package core

import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

@JsonCodec
case class FactNone() extends FactValue {
  def ===(that: FactValue): Option[Boolean] = {
    that match {
      case FactNone() => Some(true)
      case _ => Some(false)
    }
  }

  def <(that: FactValue): Option[Boolean] = Some(false)

}

object FactNone {

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, FactNone] = jsonEncoderOf[M, FactNone]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, FactNone] = jsonOf[M, FactNone]

}
