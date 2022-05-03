package zrule.engine
package core

import ConditionType._

case class DecisionCondition(factName: String, conditionType: ConditionType, value: FactValue) {
  def evaluate(variable: Variable): Option[Boolean] = {
    if (variable.factName != this.factName) None
    else this.conditionType match {
      case Eq => variable.factValue === this.value
      case Ne => variable.factValue !== this.value
      case Lt => variable.factValue < this.value
      case Lte => variable.factValue <= this.value
      case Gt => variable.factValue > this.value
      case Gte => variable.factValue >= this.value
    }
  }
}
