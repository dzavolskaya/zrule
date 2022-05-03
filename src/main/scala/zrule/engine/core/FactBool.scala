package zrule.engine
package core

import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

@JsonCodec
case class FactBool(b: Boolean) extends FactValue {
  def ===(that: FactValue): Option[Boolean] = {
    that match {
      case FactBool(b) => Some(b == this.b)
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

  override def toString: String = b.toString
}

object FactBool {

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, FactBool] = jsonEncoderOf[M, FactBool]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, FactBool] = jsonOf[M, FactBool]

}

