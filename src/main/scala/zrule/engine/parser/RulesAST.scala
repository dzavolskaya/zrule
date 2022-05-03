package zrule.engine
package parser

import scala.util.parsing.input.Positional

import core.FactValue

sealed trait RulesAST extends Positional
case class Decision(rules: List[BusinessRule]) extends RulesAST
case class BusinessRule(conditions: List[Condition], consequence: Consequence) extends RulesAST
case class Consequence(outputName: String, outputValue: FactValue) extends RulesAST

sealed trait Condition extends Positional {
  def factName: String
  def factValue: FactValue
}

case class Equals(factName: String, factValue: FactValue) extends Condition
case class NotEquals(factName: String, factValue: FactValue) extends Condition
case class GreaterThan(factName: String, factValue: FactValue) extends Condition
case class GreaterThanOrEquals(factName: String, factValue: FactValue) extends Condition
case class LowerThan(factName: String, factValue: FactValue) extends Condition
case class LowerThanOrEquals(factName: String, factValue: FactValue) extends Condition