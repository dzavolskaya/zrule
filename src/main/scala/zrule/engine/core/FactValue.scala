package zrule.engine
package core

import io.circe.{Decoder, Encoder}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec

trait FactValue {

  def ===(that: FactValue): Option[Boolean]

  def !==(that: FactValue): Option[Boolean] = {
    (this === that).map(c => !c)
  }

  def <(that: FactValue) : Option[Boolean]

  def <=(that: FactValue): Option[Boolean] = {
    that match {
      case FactNone() => Some(false)
      case _ => (this < that, this === that) match {
        case (Some(lt), Some(eq)) => Some(lt || eq)
        case _ => None
      }
    }
  }

  def >=(that: FactValue): Option[Boolean] = {
    that match {
      case FactNone() => Some(false)
      case _ => (this < that).map(c => !c)
    }
  }

  def >(that: FactValue): Option[Boolean] = {
    that match {
      case FactNone() => Some(false)
      case _ => (this >= that, this !== that) match {
        case (Some(gte), Some(ne)) => Some(gte && ne)
        case _ => None
      }
    }
  }
}

object FactValue {
  implicit def eitherDecoder[A, B](implicit a: Decoder[A], b: Decoder[B]): Decoder[Either[A, B]] = {
    val left:  Decoder[Either[A, B]] = a.map(Left(_))
    val right: Decoder[Either[A, B]] = b.map(Right(_))
    left or right
  }

  implicit val decodeNumberOrString: Decoder[Either[Double, String]] =
    Decoder[Double].map(Left(_)).or(Decoder[String].map(Right(_)))

  implicit val decodeBoolOrNumberOrString: Decoder[Either[Boolean, Either[Double, String]]] =
    Decoder.decodeBoolean.or(
      Decoder.decodeString.emap {
        case "true" => Right(true)
        case "false" => Right(false)
        case _ => Left("Boolean")
      }
    ).map(Left(_)).or(decodeNumberOrString.map(Right(_)))

  implicit val decoderFactValue: Decoder[FactValue] = decodeBoolOrNumberOrString.emap {
    case Left(value) => Right(FactBool(value))
    case Right(Right(value)) => Right(FactString(value))
    case Right(Left(value)) if isValidNumber(value.toString) => Right(FactNumber(value))
    case Right(Left(value)) if !isValidNumber(value.toString) => Right(FactNone()) // Workaround for null converted to NaN number by decoder - TODO
    case unexpected => Left(s"Invalid fact value: $unexpected")
  }

  implicit val encoderFactValue: Encoder[FactValue] = Encoder[String].contramap {
    case FactString(s) => s
    case FactBool(b) => b.toString
    case FactNumber(n) => n.toString
    case FactNone() => "none"
    case _ => "*invalid word*"
  }

  def isValidNumber(n: String): Boolean = {
    n.toDoubleOption match {
      case Some(v) => !v.isNaN
      case None => false
    }
  }

}
