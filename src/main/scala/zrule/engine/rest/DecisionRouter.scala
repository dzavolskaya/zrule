package zrule.engine
package rest

import cats.effect._
import cats.implicits._
import doobie.postgres.implicits._
import org.http4s.dsl.io._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import service.DecisionService
import zrule.engine.core.{SolverPolicy, VariableForm}

class DecisionRouter[M[_]: Sync] extends Http4sDsl[M]
  with ApiImplicits[M]
  with ServiceErrorHandler[M] {

  def decisionRoute(
    implicit clock: Clock[M],
    service: DecisionService[M],
  ): HttpRoutes[M] = HttpRoutes.of[M] {

    case GET -> Root =>
      Ok(JsonResponse(BaseResource.Response(service.greeting))).handleErrorWith(handleErrors)

    case GET -> Root / "decisions" :? NameQueryParamMatcher(name) +& TenantQueryParamMatcher(tenant) +& VersionQueryParamMatcher(maybeVersion) =>
      (for {
        result <- service.getDecisions(name = name, tenant = tenant, version = maybeVersion)
        response <- Ok(JsonResponse(result.map(DecisionDeploymentResponse.fromModel)))
      } yield response).handleErrorWith(handleErrors)

    case GET -> Root / "decisions" / IntVar(deploymentId) =>
      (for {
        result <- service.getDecisionById(deploymentId)
        response <- Ok(JsonResponse(result.map(DecisionDeploymentResponse.fromModel)))
      } yield response).handleErrorWith(handleErrors)

    case GET -> Root / "decisions" / IntVar(deploymentId) / "variables" =>
      (for {
        result <- service.getVariables(deploymentId)
        response <- Ok(JsonResponse(result.map(VariableResource.fromModel)))
      } yield response).handleErrorWith(handleErrors)

    case req @ POST -> Root / "decisions" =>
      val result = for {
        resource <- req.as[DecisionResource]
        _ <- service.checkDecisionResource(resource.name, Some(resource.tenant))
        _ <- service.insertDecision(resource.name, resource.tenant, resource.code)
        response <- NoContent()
      } yield response

      result.handleErrorWith(handleErrors)

    case req @ POST -> Root / "decisions" / "variables" =>
      (for {
        resource <- req.as[CodeResource]
        _ <- service.checkDecisionResource(resource.code)
        result <- service.getVariables(resource.code)
        response <- Ok(JsonResponse(result.map(VariableResource.fromModel)))
      } yield response).handleErrorWith(handleErrors)

    case req @ POST -> Root / "decisions" / IntVar(deploymentId) / "solution" :? HitPolicyQueryParamMatcher(policy) =>
      val hitPolicy = SolverPolicy.fromString(policy.getOrElse("all"))
      val result = for {
        resource <- req.as[SolutionResource.Resource]
        variables = resource.variables.map(VariableForm.fromResource).map(VariableForm.toVariable)
        result <- service.getSolutionFrom(deploymentId, variables, hitPolicy)
        response <- Ok(JsonResponse(result.map(SolutionResource.Response.fromModel)))
      } yield response

      result.handleErrorWith(handleErrors)

    case req @ POST -> Root / "decisions" / "solution" :? HitPolicyQueryParamMatcher(policy) =>
      val hitPolicy = SolverPolicy.fromString(policy.getOrElse("all"))
      val result = for {
        resource <- req.as[SolutionResource.CompleteResource]
        variables = resource.variables.map(VariableForm.fromResource).map(VariableForm.toVariable)
        result <- service.getSolutionFrom(resource.code, variables, hitPolicy)
        response <- Ok(JsonResponse(result.map(SolutionResource.Response.fromModel)))
      } yield response

      result.handleErrorWith(handleErrors)

  }

}

object NameQueryParamMatcher extends QueryParamDecoderMatcher[String]("name")
object TenantQueryParamMatcher extends QueryParamDecoderMatcher[String]("tenant")
object VersionQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("version")
object HitPolicyQueryParamMatcher extends OptionalQueryParamDecoderMatcher[String]("hitPolicy")
