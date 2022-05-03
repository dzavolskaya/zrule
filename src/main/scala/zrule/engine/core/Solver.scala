package zrule.engine
package core

import SolverPolicy._

class Solver(rules: List[DecisionRule]) { self =>

  private def handleRule(rule: DecisionRule, variables: List[Variable]): Either[SolverError, Option[DecisionConsequence]] = {
    rule.decisionConditions.foldRight(Right(None): Either[SolverError, Option[DecisionConsequence]]) {
      (c, acc) => {
        variables.find(v => v.factName == c.factName) match {
          case None => Left(SolverError(s"Cannot find ${c.factName} in the list of variables"))
          case Some(v) => c.evaluate(v) match {
            case None => Left(SolverError(s"Cannot evaluate condition $c with variable $v"))
            case Some(b) => if(!b) return Right(None) else acc.fold(e => Left(e), _ => Right(Some(rule.consequence)))
          }
        }
      }
    }
  }

  def solve(variables: List[Variable], policy: SolverPolicy): Either[SolverError, List[DecisionConsequence]] = {
    val matches = self.rules
      .foldRight(Right(Nil): Either[SolverError, List[DecisionConsequence]]) {
        (r, acc) => handleRule(r, variables) match {
          case Left(e) => Left(e)
          case Right(Some(consequence)) => acc.fold(err => Left(err), prev => Right(consequence :: prev))
          case Right(None) => acc
        }
      }

    matches match {
      case Left(e) => Left(e)
      case Right(l) => policy match {
        case First => Right(if (l.isEmpty) l else List(l.head))
        case All => Right(l)
        case Unique =>
          if (l.map(_.value).distinct.size != l.map(_.value).size)
            Left(SolverError("Unique solver policy is requested but multiple values have been found"))
          else Right(l)
      }
    }
  }
}

