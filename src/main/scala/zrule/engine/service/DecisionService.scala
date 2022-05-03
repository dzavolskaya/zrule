package zrule.engine
package service

import java.time.LocalDateTime.now

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import core.{DecisionConsequence, DecisionDeployment, DecisionRule, Solver, SolverError, Variable, VariableForm}
import core.SolverPolicy.SolverPolicy
import persistence.DeploymentQuery
import parser.{LexerError, ParserError, RulesCompiler, SemanticAnalyserError}
import rest.{BadRequestError, InternalError, NotFoundError}

class DecisionService[M[_]](
  implicit xa: Transactor[M],
  bracket: Bracket[M, Throwable],
) {

  def getDecisions(
    name: String,
    tenant: String,
    version: Option[Int]
  )(implicit read: Read[DecisionDeployment]): M[List[DecisionDeployment]] =
    DeploymentQuery.getByNameAndTenant(name, tenant, version).to[List].transact(xa)

  def createDeployment(decisionDeployment: DecisionDeployment): M[Int] = {
    for {
      count <- DeploymentQuery.insert(decisionDeployment).run.transact(xa)
      _ <- {
        if (count == 0) bracket.raiseError[Unit](BadRequestError(s"Something went wrong during insertion of $decisionDeployment"))
        else bracket.pure(())
      }
    } yield count
  }

  def getDecisionById(id: Int): M[Option[DecisionDeployment]] = DeploymentQuery.getById(id).option.transact(xa)

  def getLastVersionByName(name: String, tenant: String): M[Option[DecisionDeployment]] = {
    DeploymentQuery.getLastVersionByNameAndTenant(name, tenant).option.transact(xa)
  }

  def getId: M[Option[Int]] = DeploymentQuery.getLatestId.option.transact(xa)

  def getRules(deploymentId: Int): M[List[DecisionRule]] = {
    for {
      decisionOpt <- getDecisionById(deploymentId)
      rules <- decisionOpt match {
        case Some(decision) => compileDecisionCode(decision.code)
        case None => bracket.raiseError(NotFoundError(s"No decision found for #$deploymentId"))
      }
    } yield rules
  }

  def compileDecisionCode(decisionCode: String): M[List[DecisionRule]] = {
    val rules = RulesCompiler.apply(decisionCode).liftTo[M]

    rules.recoverWith{
      case e: LexerError => bracket.raiseError(InternalError(s"Lexer error: ${e.msg}. Compiler failed to parse the following code: $decisionCode"))
      case e: ParserError => bracket.raiseError(InternalError(s"Syntax error: ${e.msg}. Compiler failed to parse the following code: $decisionCode"))
      case e: SemanticAnalyserError => bracket.raiseError(InternalError(s"Error: ${e.msg}. Compiler failed to semantically validate the following code: $decisionCode"))
      case _ => bracket.raiseError(InternalError(s"Compiler failed to parse the following code: $decisionCode"))
    }
  }

  def getVariables(code: String): M[List[VariableForm]] = {
    for {
      rules <- compileDecisionCode(code)
      variables = VariableForm.fromRules(rules)
    } yield variables
  }

  def getVariables(deploymentId: Int): M[List[VariableForm]] = {
    for {
      rules <- getRules(deploymentId)
      variables = VariableForm.fromRules(rules)
    } yield variables
  }

  def greeting: String = s"Welcome to zrule - business rules engine"

  def insertDecision(
    name: String,
    tenant: String,
    code: String
  ): M[Int] = {
    val inserted = for {
      _ <- compileDecisionCode(code)
      previousOpt <- getLastVersionByName(name = name, tenant = tenant)
      latestId <- getId
      id = latestId.fold(1)(id => id + 1)
      newDeployment = previousOpt match {
        case Some(value) =>
          DecisionDeployment(
            id = id,
            name = value.name,
            tenant = value.tenant,
            version = value.version + 1,
            date = now(),
            code = code
          )
        case None =>
          DecisionDeployment(
            id = id,
            name = name,
            tenant = tenant,
            version = 1,
            date = now(),
            code = code
          )
      }
      count = createDeployment(newDeployment)
    } yield count

    inserted.flatten

  }

  def checkDecisionResource(arg1: String, arg2: Option[String] = None): M[Unit] = {
    val validated = (arg1, arg2) match {
        case (arg1, Some(arg2)) if arg1.isEmpty || arg2.isEmpty => Left(BadRequestError("Data is empty or malformed"))
        case (arg1, None) if arg1.isEmpty => Left(BadRequestError("Data is empty or malformed"))
        case _ => Right(())
      }
    validated.liftTo[M]
  }

  def getSolutionFrom(deploymentId: Int, variables: List[Variable], hitPolicy: SolverPolicy): M[List[DecisionConsequence]] = {
    for {
      rules <- getRules(deploymentId)
      solver = new Solver(rules)
      consequences <- {
        val result = solver.solve(variables, hitPolicy).liftTo[M]
        result.recoverWith{
          case e:SolverError => bracket.raiseError(InternalError(s"Solver Error: ${e.msg}. Failed to parse and validate the variables."))
        }
      }
    } yield consequences
  }

  def getSolutionFrom(code: String, variables: List[Variable], hitPolicy: SolverPolicy): M[List[DecisionConsequence]] = {
    for {
      rules <- compileDecisionCode(code)
      solver = new Solver(rules)
      consequences <- {
        val result = solver.solve(variables, hitPolicy).liftTo[M]
        result.recoverWith{
          case e:SolverError => bracket.raiseError(InternalError(s"Solver Error: ${e.msg}. Failed to parse and validate the variables."))
        }
      }
    } yield consequences
  }

}
