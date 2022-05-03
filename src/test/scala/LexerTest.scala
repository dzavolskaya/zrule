import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Inside._
import org.scalatest.matchers.should.Matchers._

import zrule.engine.parser._

class LexerTest extends AnyFunSpec {

  describe("A lexer") {
    describe("when dealing with a valid code") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and allergy ne "aspirin" then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("allergy"), Ne(), Literal("aspirin", LiteralType.String_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )

        inside(result) { case Right(r) =>
          r should equal (expected)
        }
      }
    }
    describe("when dealing with an invalid identifier") {
      it("should return a lexer error") {
        val code = """when foo? eq "bar" then bar is "foo" end"""
        val result = RulesLexer.apply(code)
        inside(result) {
          case Left(err) => err shouldBe a [LexerError]
        }
      }
    }
    describe("when dealing with a valid code having a true boolean literal") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and hypertension ne true then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("hypertension"), Ne(), Literal("true", LiteralType.Bool_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )

        inside(result) { case Right(r) =>
          r should equal (expected)
        }
      }
    }
    describe("when dealing with a valid code having a false boolean literal") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and hypertension eq false then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("hypertension"), Eq(), Literal("false", LiteralType.Bool_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )

        inside(result) { case Right(r) =>
          r should equal (expected)
        }
      }
    }
    describe("when dealing with a valid code having a none literal") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and record ne none then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("record"), Ne(), Literal("", LiteralType.None_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )

        inside(result) { case Right(r) =>
          r should equal (expected)
        }
      }
    }
    describe("when dealing with a valid code having a number literal") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and weight gte 56.5 then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("weight"), Gte(), Literal("56.5", LiteralType.Number_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )

        inside(result) { case Right(r) =>
          r should equal (expected)
        }
      }
    }
    describe("when dealing with a valid code having a negative number literal") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and weight gte -56.5 then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("weight"), Gte(), Literal("-56.5", LiteralType.Number_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )
        inside(result) {
          case Right(r) => r should equal (expected)
        }
      }
    }
    describe("when dealing with a valid code having an integer like number literal") {
      it("should return a list of tokens") {
        val code = """when symptom eq "headache" and weight gte 56 then treatment is "aspirin" end"""
        val result = RulesLexer.apply(code)
        val expected: List[RuleToken] = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("weight"), Gte(), Literal("56", LiteralType.Number_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )
        inside(result) {
          case Right(r) => r should equal (expected)
        }
      }
    }
  }
}
