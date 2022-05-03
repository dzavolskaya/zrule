package zrule.engine
package parser

import core.ConditionType._
import core.{DecisionCondition, DecisionConsequence, DecisionRule}

object RulesTransformer {

  private def transformCondition(astElement: Condition): DecisionCondition = {
    astElement match {
      case Equals(n, v) => DecisionCondition(n, Eq, v)
      case NotEquals(n, v) => DecisionCondition(n, Ne, v)
      case GreaterThan(n, v) => DecisionCondition(n, Gt, v)
      case GreaterThanOrEquals(n, v) => DecisionCondition(n, Gte, v)
      case LowerThan(n, v) => DecisionCondition(n, Lt, v)
      case LowerThanOrEquals(n, v) => DecisionCondition(n, Lte, v)
    }
  }

  private def transformConsequence(astElement: Consequence): DecisionConsequence = {
    DecisionConsequence(astElement.outputName, astElement.outputValue)
  }

  private def transformRule(astElement: BusinessRule): DecisionRule = {
    DecisionRule(astElement.conditions.map(transformCondition), transformConsequence(astElement.consequence))
  }

  def apply(ast: Decision): Either[TransformerError, List[DecisionRule]] = {
    Right(ast.rules.map(transformRule))
  }
}
