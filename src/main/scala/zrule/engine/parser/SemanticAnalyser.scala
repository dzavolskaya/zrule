package zrule.engine
package parser

import core.{FactValue,FactNone,FactNumber,FactString,FactBool}

object SemanticAnalyser {

  def apply(decision: Decision): Either[SemanticAnalyserError, Decision] = validate(decision)

  protected def validate(ast: Decision): Either[SemanticAnalyserError, Decision] = {
    val validatedTypes: List[Either[SemanticAnalyserError, FactValue]] = validateComparatorType(ast.rules)
    val validatedIdentifiers: List[Either[SemanticAnalyserError, Condition]] = validateIdentifiers(ast.rules)

    val validatedTypesErrors = validatedTypes collect { case Left(x) => x.msg }
    val validatedIdentifiersErrors = validatedIdentifiers collect { case Left(x) => x.msg }

    val errors: String = (validatedTypesErrors ++ validatedIdentifiersErrors).mkString(", ")

    if (errors.isEmpty) Right(ast) else Left(SemanticAnalyserError(errors))
  }

  protected  def validateComparatorType(rules: List[BusinessRule]): List[Either[SemanticAnalyserError, FactValue]] = {
    rules.flatMap{ rule =>
      rule
        .conditions
        .map(f = {
          case GreaterThan(_, factValue: FactValue) => checkNumberType(factValue)
          case LowerThan(_, factValue: FactValue) => checkNumberType(factValue)
          case GreaterThanOrEquals(_, factValue) => checkNumberType(factValue)
          case LowerThanOrEquals(_, factValue) => checkNumberType(factValue)
          case NotEquals(_, factValue) => Right(factValue)
          case Equals(_, factValue) => Right(factValue)
        })
    }
  }

  protected def validateIdentifiers(rules: List[BusinessRule]): List[Either[SemanticAnalyserError, Condition]] = {
    val allRulesConditions = rules.collect{ case BusinessRule(conditions, _) => conditions }.flatten

    val rulesConditionCheck = validateConditionList(allRulesConditions)
    val withinRuleConditionCheck = rules.flatMap { rule => validateConditionList(rule.conditions) }

    withinRuleConditionCheck ++ rulesConditionCheck
  }

  private def validateConditionList(conditionList: List[Condition]): List[Either[SemanticAnalyserError, Condition]] = {
    conditionList
      .iterator
      .scanLeft(Map.empty[String, FactValue])((map, condition) => map + (condition.factName -> condition.factValue))
      .zip(conditionList.iterator)
      .collect { case (map, condition) =>
        if (map.contains(condition.factName)) {
          val typeCheckResult = (condition.factValue, map.get(condition.factName)) match {
            case (_: FactNumber | FactNone(), Some(_: FactNumber)) => Right(condition)
            case (FactNone(), Some(FactNone())) => Right(condition)
            case (_: FactBool | FactNone(), Some(_: FactBool)) => Right(condition)
            case (_: FactString | FactNone(), Some(_: FactString)) => Right(condition)
            case _ => Left(ParserError(s"The repeated ${condition.factName} constraints have different types"))
          }

          typeCheckResult.fold(_ => Left(SemanticAnalyserError(s"The repeated ${condition.factName} constraints have different types")), condition => Right(condition))

        }
        else Right(condition)
      }
      .toList
  }

  private def checkNumberType(value: FactValue): Either[SemanticAnalyserError, FactValue] = value match {
    case FactNumber(_) => Right(value)
    case _ => Left(SemanticAnalyserError(s"Number comparison cannot accept $value"))
  }

}
