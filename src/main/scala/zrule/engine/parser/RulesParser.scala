package zrule.engine
package parser

import scala.util.parsing.combinator._
import scala.util.parsing.input._

import core.{FactValue,FactNone,FactNumber,FactString,FactBool}

import LiteralType._

object RulesParser extends Parsers {
  override type Elem = RuleToken

  class RulesTokenReader(tokens: Seq[RuleToken]) extends Reader[RuleToken] {
    override def first: RuleToken = tokens.head
    override def atEnd: Boolean = tokens.isEmpty
    override def pos: Position = NoPosition
    override def rest: Reader[RuleToken] = new RulesTokenReader(tokens.tail)
  }

  def apply(tokens: Seq[RuleToken]): Either[ParserError, Decision] = {
    val reader = new RulesTokenReader(tokens)
    program(reader) match {
      case Success(result, _) => Right(result)
      case NoSuccess(msg, _) => Left(ParserError(msg))
      case Error(msg, _) => Left(ParserError(msg))
      case Failure(msg, _) => Left(ParserError(msg))
    }
  }

  def program: Parser[Decision] = phrase(decision)

  def decision: Parser[Decision] = rep1(rule) ^^ (ruleList => Decision(ruleList))

  def rule: Parser[BusinessRule] = {
    (When() ~ condition ~ rep(And() ~ condition) ~ Then() ~ consequence ~ End()) ^^ {
      case _ ~ firstCondition ~ otherConditions ~ _ ~ consequence ~ _ => parser.BusinessRule(List(firstCondition) ++ otherConditions.map(_._2), consequence)
    }
  }

  def condition: Parser[Condition] = {
    val eq = identifier ~ Eq() ~ literal ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => Equals(id, toFactValue(value, type_))
    }

    val ne = identifier ~ Ne() ~ literal ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => NotEquals(id, toFactValue(value, type_))
    }

    val lt = identifier ~ Lt() ~ literal ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => LowerThan(id, toFactValue(value, type_))
    }

    val lte = identifier ~ Lte() ~ literal ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => LowerThanOrEquals(id, toFactValue(value, type_))
    }

    val gt = identifier ~ Gt() ~ literal ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => GreaterThan(id, toFactValue(value, type_))
    }

    val gte = identifier ~ Gte() ~ literal ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => GreaterThanOrEquals(id, toFactValue(value, type_))
    }

    eq | ne | lt | lte | gt | gte
  }

  def consequence: Parser[Consequence] = {
    (identifier ~ Is() ~ literal) ^^ {
      case Identifier(id) ~ _ ~ Literal(value, type_) => Consequence(id, toFactValue(value, type_))
    }
  }

  private def identifier: Parser[Identifier] = {
    accept("identifier", { case id @ Identifier(_) => id })
  }

  private def literal: Parser[Literal] = {
    accept("literal", { case value @ Literal(_, _) => value })
  }

  private def toFactValue(value:String, literalType: LiteralType): FactValue = {
    literalType match {
      case LiteralType.Number_ => FactNumber(value.toDouble)
      case LiteralType.Bool_ => FactBool(value.toBoolean)
      case LiteralType.None_ => FactNone()
      case LiteralType.String_ => FactString(value)
    }
  }
}