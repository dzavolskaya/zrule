package zrule.engine
package rest

import java.time.LocalDateTime

import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._
import core._

@JsonCodec
case class JsonResponse[B](body: B)

object BaseResource {

  @JsonCodec
  case class Request(
    message: String,
  )

  object Request {
    implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, Request] =
      jsonEncoderOf[M, Request]
    implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, Request] =
      jsonOf[M, Request]
  }

  @JsonCodec
  case class Response(
    message: String,
  )

}

@JsonCodec
case class DecisionDeploymentResponse(
  id: Int,
  name: String,
  tenant: String,
  version: Int,
  date: LocalDateTime,
  code: String
)

object DecisionDeploymentResponse {

  def fromModel(decision: DecisionDeployment): DecisionDeploymentResponse = DecisionDeploymentResponse(
    id = decision.id,
    name = decision.name,
    tenant = decision.tenant,
    version = decision.version,
    date = decision.date,
    code = decision.code
  )
}

@JsonCodec
case class VariableResource(
  factName: String,
  factType: String,
  factValue: FactValue,
)

object VariableResource {

  def fromModel(variable: VariableForm): VariableResource = VariableResource(
    factName = variable.factName,
    factType = variable.factType,
    factValue = variable.factValue,
  )

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, VariableResource] =
    jsonEncoderOf[M, VariableResource]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, VariableResource] =
    jsonOf[M, VariableResource]
}

@JsonCodec
case class DecisionResource(
  name: String,
  tenant: String,
  code: String
)

object DecisionResource {

  def fromModel(decision: Decision): DecisionResource = DecisionResource(
    name = decision.name,
    tenant = decision.tenant,
    code = decision.code
  )
  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, DecisionResource] = jsonEncoderOf[M, DecisionResource]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, DecisionResource] = jsonOf[M, DecisionResource]

}

@JsonCodec
case class CodeResource(
  code: String
)

object CodeResource {

  implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, CodeResource] = jsonEncoderOf[M, CodeResource]
  implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, CodeResource] = jsonOf[M, CodeResource]

}

object SolutionResource {

  @JsonCodec
  case class Resource(
    variables: List[VariableResource]
  )

  object Resource {

    implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, Resource] = jsonEncoderOf[M, Resource]
    implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, Resource] = jsonOf[M, Resource]

  }

  @JsonCodec
  case class CompleteResource(
    code: String,
    variables: List[VariableResource]
  )

  object CompleteResource {

    implicit def entityEncoder[M[_]: Sync]: EntityEncoder[M, CompleteResource] = jsonEncoderOf[M, CompleteResource]
    implicit def entityDecoder[M[_]: Sync]: EntityDecoder[M, CompleteResource] = jsonOf[M, CompleteResource]

  }

  @JsonCodec
  case class Response(
    factName: String,
    factValue: FactValue
  )

  object Response {

    def fromModel(consequence: DecisionConsequence): Response = Response(
      factName = consequence.name,
      factValue = consequence.value,
    )
  }

}

