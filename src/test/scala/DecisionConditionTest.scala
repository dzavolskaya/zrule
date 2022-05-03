import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

import zrule.engine.core.{DecisionCondition, FactBool, FactString, Variable}
import zrule.engine.core.ConditionType._

class DecisionConditionTest extends AnyFunSpec {
  private val variable = Variable("symptom", FactString("headache"))

  describe("A condition equals a variable") {

    describe("when checking equality") {
      val condition = DecisionCondition("symptom", Eq, FactString("headache"))
      it("should return true") {
        condition.evaluate(variable) should be (Some(true))
      }
    }

    describe("when checking inequality") {
      val condition = DecisionCondition("symptom", Ne, FactString("headache"))
      it("should return true") {
        condition.evaluate(variable) should be (Some(false))
      }
    }
  }

  describe("A condition and a variable have a different fact name") {
    it("should return None") {
      val condition = DecisionCondition("allergy", Eq, FactString("aspirin"))
      condition.evaluate(variable) should be (None)
    }
  }

  describe("A condition and a variable have a different type of fact") {
    it("should return None") {
      val condition = DecisionCondition("symptom", Eq, FactBool(true))
      condition.evaluate(variable) should be (None)
    }
  }
}
