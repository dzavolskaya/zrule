import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Inside._
import org.scalatest.matchers.should.Matchers._

import zrule.engine.parser.{RulesCompiler, SemanticAnalyserError, ParserError, LexerError}
import zrule.engine.core.{DecisionCondition, DecisionConsequence, DecisionRule, FactBool, FactNone, FactNumber, FactString}
import zrule.engine.core.ConditionType._

class CompilerTest extends AnyFunSpec {
  describe("A compiler") {
    describe("when dealing with valid code") {
      it("should return the expected core structure") {
        val code =
          """
            |when symptom eq "headache" and allergy ne "aspirin" then treatment is "aspirin" end
            |when symptom eq "headache" and allergy eq "aspirin" then treatment is "paracetamol" end
          """.stripMargin.trim

        val expected = List(
          DecisionRule(
            List(
              DecisionCondition("symptom", Eq, FactString("headache")),
              DecisionCondition("allergy", Ne, FactString("aspirin"))
            ),
            DecisionConsequence("treatment", FactString("aspirin"))
          ),
          DecisionRule(
            List(
              DecisionCondition("symptom", Eq, FactString("headache")),
              DecisionCondition("allergy", Eq, FactString("aspirin"))
            ),
            DecisionConsequence("treatment", FactString("paracetamol"))
          )
        )

        val actual = RulesCompiler.apply(code)

        inside(actual) {
          case Right(r) => r should equal (expected)
        }
      }
    }

    describe("when dealing with a highly typed code") {
      it("should return the expected AST") {
        val code =
          """
            |when symptom eq "headache"
            |and weight lt 56.5
            |and height gt 1.83
            |and record ne none
            |and hypertension ne true
            |and cholesterol ne false
            |then treatment is "aspirin"
            |end
          """.stripMargin.trim

        val expected =  List(
          DecisionRule(
            List(
              DecisionCondition("symptom", Eq, FactString("headache")),
              DecisionCondition("weight", Lt, FactNumber(56.5)),
              DecisionCondition("height", Gt, FactNumber(1.83)),
              DecisionCondition("record", Ne, FactNone()),
              DecisionCondition("hypertension", Ne, FactBool(true)),
              DecisionCondition("cholesterol", Ne, FactBool(false))
            ),
            DecisionConsequence("treatment", FactString("aspirin"))
          )
        )

        val actual = RulesCompiler.apply(code)

        inside(actual) {
          case Right(r) => r should equal (expected)
        }
      }
    }

    describe("when dealing with syntactically incorrect code") {
      it("should return a lexer error") {
        val code =
          """
            |when !#??? eq "headache" and allergy ne "aspirin" then treatment is "aspirin" end
            |when symptom eq "headache" and allergy eq "aspirin" then treatment is "paracetamol" end
          """.stripMargin.trim

        val result = RulesCompiler.apply(code)

        inside(result) {
          case Left(err) => err shouldBe a [LexerError]
        }
      }
    }

    describe("when dealing with a code where termination character is missing") {
      it("should return a parser error") {
        val code =
          """
            |when symptom eq "headache" and allergy ne "aspirin" then treatment is "aspirin"
            |when symptom eq "headache" and allergy eq "aspirin" then treatment is "paracetamol" end
          """.stripMargin.trim

        val result = RulesCompiler.apply(code)

        inside(result) {
          case Left(err) => err shouldBe a [ParserError]
        }
      }
    }

    describe("when dealing with a code where a literal quote is missing") {
      it("should return a lexer error") {
        val code =
          """
            |when symptom eq "headache and allergy ne "aspirin" then treatment is "aspirin" end
            |when symptom eq "headache" and allergy eq "aspirin" then treatment is "paracetamol" end
          """.stripMargin.trim

        val result = RulesCompiler.apply(code)

        inside(result) {
          case Left(err) => err shouldBe a [LexerError]
        }
      }
    }

    describe("when dealing with a code where a when keyword is missing") {
      it("should return a parser error") {
        val code =
          """
            |when symptom eq "headache" and allergy ne "aspirin" then treatment is "aspirin" end
            |symptom eq "headache" and allergy eq "aspirin" then treatment is "paracetamol" end
          """.stripMargin.trim

        val result = RulesCompiler.apply(code)

        inside(result) {
          case Left(err) => err shouldBe a [ParserError]
        }
      }
    }

    describe("when dealing with a code where a then keyword is missing") {
      it("should return a parser error") {
        val code =
          """
            |when symptom eq "headache" and allergy ne "aspirin" then treatment is "aspirin" end
            |when symptom eq "headache" and allergy eq "aspirin" treatment is "paracetamol" end
          """.stripMargin.trim

        val result = RulesCompiler.apply(code)

        inside(result) {
          case Left(err) => err shouldBe a [ParserError]
        }
      }
    }

    describe("when dealing with semantically incorrect code") {
      it("should return a semantic analyser error") {
        val code =
          """
            |when symptom eq "headache"
            |and weight gte "overweight"
            |and record ne none
            |and hypertension ne true
            |and cholesterol ne false
            |then treatment is "aspirin"
            |end
          """.stripMargin.trim

        val result = RulesCompiler.apply(code)

        inside(result) {
          case Left(err) => err shouldBe a [SemanticAnalyserError]
        }
      }
    }
  }
}
