package zrule.engine
package parser

import core.DecisionRule

object RulesCompiler {
  def apply(code: String): Either[CompilerError, List[DecisionRule]] = {
    for {
      tokens <- RulesLexer(code)
      ast <- RulesParser(tokens)
      validatedAst <- SemanticAnalyser(ast)
      rules <- RulesTransformer(validatedAst)
    } yield rules
  }
}
