import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Inside._
import org.scalatest.matchers.should.Matchers._

import zrule.engine.core.FactString
import zrule.engine.parser._

class ParserTest extends AnyFunSpec {
  describe("A parser") {
    describe("when dealing with a simple when / then list of tokens") {
      it("should return the expected AST") {
        val tokens = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )
        val result = RulesParser.apply(tokens)
        val expected = Decision(
          List(BusinessRule(List(Equals("symptom", FactString("headache"))), Consequence("treatment", FactString("aspirin"))))
        )

        inside(result) {
          case Right(r) => r should equal (expected)
        }
      }
    }

    describe("when dealing with a when / and / then list of tokens") {
      it("should return the expected AST") {
        val tokens = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("allergy"), Ne(), Literal("aspirin", LiteralType.String_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End()
        )
        val result = RulesParser.apply(tokens)
        val expected = Decision(
          List(BusinessRule(
            List(Equals("symptom", FactString("headache")), NotEquals("allergy", FactString("aspirin"))), Consequence("treatment", FactString("aspirin"))
          ))
        )

        inside(result) {
          case Right(r) => r should equal (expected)
        }
      }
    }

    describe("when dealing with a sequence of when / and / then list of tokens") {
      it("should return the expected AST") {
        val tokens = List(
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("allergy"), Ne(), Literal("aspirin", LiteralType.String_),
          Then(), Identifier("treatment"), Is(), Literal("aspirin", LiteralType.String_),
          End(),
          When(), Identifier("symptom"), Eq(), Literal("headache", LiteralType.String_),
          And(), Identifier("allergy"), Eq(), Literal("aspirin", LiteralType.String_),
          Then(), Identifier("treatment"), Is(), Literal("paracetamol", LiteralType.String_),
          End()
        )
        val result = RulesParser.apply(tokens)
        val expected = Decision(
          List(
            BusinessRule(
              List(Equals("symptom", FactString("headache")), NotEquals("allergy", FactString("aspirin"))), Consequence("treatment", FactString("aspirin"))
            ),
            BusinessRule(
              List(Equals("symptom", FactString("headache")), Equals("allergy", FactString("aspirin"))), Consequence("treatment", FactString("paracetamol"))
            ),
          )
        )

        inside(result) {
          case Right(r) => r should equal (expected)
        }
      }
    }
  }
}
