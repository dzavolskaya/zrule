package zrule.engine
package core

import rest.VariableResource

case class VariableForm(factName: String, factType: String, factValue: FactValue = FactNone()) {
  def toVariable: Variable = Variable(this.factName, this.factValue)
}

object VariableForm {
  private def fromCondition(condition: DecisionCondition): Option[VariableForm] = condition.value match {
    case FactNone() => None
    case FactString(_) => Some(VariableForm(condition.factName, "string"))
    case FactNumber(_) => Some(VariableForm(condition.factName, "number"))
    case FactBool(_) => Some(VariableForm(condition.factName, "bool"))
  }

  def fromRules(rules: List[DecisionRule]): List[VariableForm] =
    rules
      .flatMap(r => r.decisionConditions.flatMap(fromCondition))
      .distinct

  def toVariable(form: VariableForm): Variable = Variable(form.factName, form.factValue)

  def fromResource(resource: VariableResource): VariableForm = VariableForm(
    factName = resource.factName,
    factType = resource.factType,
    factValue = resource.factValue,
  )
}
