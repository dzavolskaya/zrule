package zrule.engine
package parser

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers

object RulesLexer extends RegexParsers {

  override def skipWhitespace = true
  override val whiteSpace: Regex = "[ \t\r\f\n]+".r

  def apply(code: String): Either[LexerError, List[RuleToken]] = {
    parse(tokens, code.toLowerCase()) match {
      case Success(result, _) => Right(result)
      case NoSuccess(msg, _) => Left(LexerError(msg))
      case Error(msg, _) => Left(LexerError(msg))
      case Failure(msg, _) => Left(LexerError(msg))
    }
  }

  def identifier: Parser[Identifier] = {
    """[a-z0-9_]+""".r ^^ {str => Identifier(str)}
  }

  def literal: Parser[Literal] = {
    val string_ = """"[^"]*"""".r ^^ {
      str => Literal(str.substring(1, str.length - 1), LiteralType.String_)
    }

    val number_ = """[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)""".r ^^ {
      str => Literal(str, LiteralType.Number_)
    }

    val true_ = """true""".r ^^ {
      str => Literal(str, LiteralType.Bool_)
    }

    val false_ = """false""".r ^^ {
      str => Literal(str, LiteralType.Bool_)
    }

    val none_ = """none""".r ^^ {
      str => Literal("", LiteralType.None_)
    }

    none_ | false_ | true_ | number_ | string_
  }

  def when: Parser[When] = "when" ^^ (_ => When())
  def and: Parser[And] = "and" ^^ (_ => And())
  def then_ :Parser[Then] = "then" ^^ (_ => Then())
  def is: Parser[Is] = "is" ^^ (_ => Is())
  def eq: Parser[Eq] = "eq" ^^ (_ => Eq())
  def ne: Parser[Ne] = "ne" ^^ (_ => Ne())
  def gt: Parser[Gt] = "gt" ^^ (_ => Gt())
  def gte: Parser[Gte] = "gte" ^^ (_ => Gte())
  def lt: Parser[Lt] = "lt" ^^ (_ => Lt())
  def lte: Parser[Lte] = "lte" ^^ (_ => Lte())
  def end: Parser[End] = "end" ^^ (_ => End())

  def tokens: Parser[List[RuleToken]] = {
    phrase(rep1(when | and | then_ | is | eq | ne | gte | gt | lte | lt | end | literal | identifier ))
  }

}
