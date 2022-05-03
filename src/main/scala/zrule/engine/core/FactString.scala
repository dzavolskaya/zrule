package zrule.engine
package core

import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

@JsonCodec
case class FactString(s: String) extends FactValue {
  def ===(that: FactValue): Option[Boolean] = {
    that match {
      case FactString(s) => Some(s == this.s)
      case FactNone() => Some(false)
      case _ => None
    }
  }

  def <(that: FactValue): Option[Boolean] = {
    that match {
      case FactNone() => Some(false)
      case _ => None
    }
  }

  override def toString: String = s
}

object FactString {

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, FactString] = jsonEncoderOf[M, FactString]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, FactString] = jsonOf[M, FactString]

}
