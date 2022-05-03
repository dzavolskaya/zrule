package zrule.engine
package parser

object LiteralType extends Enumeration {
  type LiteralType = Value
  val String_, Number_, None_, Bool_ = Value
}

import LiteralType._

sealed trait RuleToken
case class Identifier(str: String) extends RuleToken
case class Literal(str: String, literalType: LiteralType) extends RuleToken
case class When() extends RuleToken
case class And() extends RuleToken
case class Then() extends RuleToken
case class Is() extends RuleToken
case class Eq() extends RuleToken
case class Ne() extends RuleToken
case class Gt() extends RuleToken
case class Gte() extends RuleToken
case class Lt() extends RuleToken
case class Lte() extends RuleToken
case class End() extends RuleToken
