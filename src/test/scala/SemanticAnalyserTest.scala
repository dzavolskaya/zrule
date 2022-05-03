import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._
import org.scalatest.Inside.inside

import zrule.engine.parser._
import zrule.engine.core.{FactBool, FactNone, FactNumber, FactString}

class SemanticAnalyserTest extends AnyFunSpec {
  describe("SemanticAnalyser") {
    describe("when checking AST") {
      it("should return valid AST if all the comparator types are coherent and valid") {
        val decision = Decision(
          List(
            BusinessRule(
              List(
                Equals("symptom", FactString("headache")),
                LowerThan("weight", FactNumber(56.5)),
                GreaterThan("height", FactNumber(1.83)),
                NotEquals("record", FactNone()),
                NotEquals("hypertension", FactBool(true)),
                NotEquals("cholesterol", FactBool(false))
              ),
              Consequence("treatment", FactString("aspirin"))
            )
          )
        )
        val result = SemanticAnalyser.apply(decision)

        result should equal (Right(decision))
      }

      it("should return parser error when trying to compare numerical values with boolean, none or string") {
        val decision = Decision(
          List(
            BusinessRule(
              List(
                Equals("symptom", FactString("headache")),
                LowerThan("weight", FactString("small")),
                GreaterThan("cholesterol", FactBool(true)),
                GreaterThanOrEquals("height", FactNone()),
              ),
              Consequence("treatment", FactString("aspirin"))
            )
          )
        )

        val result = SemanticAnalyser.apply(decision)

        inside(result) {
          case Left(errors) =>
            errors should matchPattern { case SemanticAnalyserError(_) => }
        }
      }

      it("should return valid AST if all the repeated identifiers within one rule have the same type") {
        val decision = Decision(
          List(
            BusinessRule(
              List(
                Equals("symptom", FactString("headache")),
                LowerThan("weight", FactNumber(56.5)),
                NotEquals("record", FactNone()),
                NotEquals("weight", FactNumber(150)),
              ),
              Consequence("treatment", FactString("aspirin"))
            )
          )
        )
        val result = SemanticAnalyser.apply(decision)

        result should equal (Right(decision))
      }

      it("should throw an error if repeated identifiers within the same rule have different type") {
        val decision = Decision(
          List(
            BusinessRule(
              List(
                Equals("symptom", FactString("headache")),
                LowerThan("weight", FactNumber(56.5)),
                NotEquals("record", FactNone()),
                NotEquals("weight", FactString("overweight")),
              ),
              Consequence("treatment", FactString("aspirin"))
            )
          )
        )
        val result = SemanticAnalyser.apply(decision)

        inside(result) {
          case Left(errors) =>
            errors should matchPattern { case SemanticAnalyserError(_) => }
        }
      }

      it("should return valid AST if all the repeated identifiers across all rules have the same type") {
        val decision = Decision(
          List(
            BusinessRule(
              List(
                Equals("symptom", FactString("headache")),
                LowerThan("weight", FactNumber(56.5)),
                NotEquals("record", FactNone()),
                LowerThan("cholesterol", FactNumber(100)),
              ),
              Consequence("treatment", FactString("aspirin"))
            ),
            BusinessRule(
              List(
                Equals("symptom", FactString("migraine")),
                NotEquals("weight", FactNumber(150)),
                Equals("cholesterol", FactNone()),
              ),
              Consequence("treatment", FactString("aspirin"))
            )
          )
        )
        val result = SemanticAnalyser.apply(decision)

        result should equal (Right(decision))
      }

      it("should throw an error if repeated identifiers across all rules have different type") {
        val decision = Decision(
          List(
            BusinessRule(
              List(
                Equals("symptom", FactString("headache")),
                LowerThan("weight", FactNumber(56.5)),
                LowerThan("cholesterol", FactNumber(100)),
                NotEquals("record", FactNone()),
              ),
              Consequence("treatment", FactString("aspirin"))
            ),
            BusinessRule(
              List(
                Equals("symptom", FactString("migraine")),
                NotEquals("weight", FactString("overweight")),
                Equals("cholesterol", FactString("low")),
              ),
              Consequence("treatment", FactString("aspirin"))
            )
          )
        )
        val result = SemanticAnalyser.apply(decision)

        inside(result) {
          case Left(errors) =>
            errors should matchPattern { case SemanticAnalyserError(_) => }
        }
      }
    }
  }
}
