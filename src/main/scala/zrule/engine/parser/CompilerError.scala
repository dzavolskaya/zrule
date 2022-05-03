package zrule.engine
package parser

trait CompilerError extends Exception

case class LexerError(msg: String) extends CompilerError
case class ParserError(msg: String) extends CompilerError
case class SemanticAnalyserError(msg: String) extends CompilerError
case class TransformerError(msg: String) extends CompilerError
