package zrule.engine
package core

import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

@JsonCodec
case class FactNumber(d: Double) extends FactValue {
  def ===(that: FactValue): Option[Boolean] = {
    that match {
      case FactNumber(n) => Some(n == this.d)
      case FactNone() => Some(false)
      case _ => None
    }
  }

  def <(that: FactValue): Option[Boolean] = {
    that match {
      case FactNumber(n) => Some(this.d < n)
      case FactNone() => Some(false)
      case _ => None
    }
  }

  override def toString: String = d.toString
}

object FactNumber {

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, FactNumber] = jsonEncoderOf[M, FactNumber]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, FactNumber] = jsonOf[M, FactNumber]

}
