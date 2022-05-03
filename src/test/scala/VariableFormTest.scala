import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

import zrule.engine.core.{DecisionCondition, DecisionConsequence, DecisionRule, FactBool, FactNone, FactNumber, FactString, VariableForm}
import zrule.engine.core.ConditionType._

class VariableFormTest extends AnyFunSpec {
  describe("A list of simple decision rules") {
    describe("when evaluating the corresponding variables") {
      it("should return a list or variable forms") {
        val rules = List(
          DecisionRule(
            List(
              DecisionCondition("symptom", Eq, FactString("headache")),
              DecisionCondition("age", Lt, FactNumber(15)),
              DecisionCondition("weight", Lt, FactNumber(50.5)),
              DecisionCondition("known", Eq, FactBool(true))
            ),
            DecisionConsequence("treatment", FactString("paracetamol"))
          )
        )

        val actual = VariableForm.fromRules(rules)
        val expected = List(
          VariableForm("symptom", "string"),
          VariableForm("age", "number"),
          VariableForm("weight", "number"),
          VariableForm("known", "bool"),
        )

        actual should equal (expected)
      }
    }
  }

  describe("A list of decision rules where none checking are implemented") {
    describe("when evaluating the corresponding variables") {
      it("should return a list or variable forms") {
        val rules = List(
          DecisionRule(
            List(
              DecisionCondition("symptom", Eq, FactString("headache")),
              DecisionCondition("age", Lt, FactNumber(15)),
              DecisionCondition("weight", Lt, FactNumber(50.5)),
              DecisionCondition("known", Eq, FactBool(true))
            ),
            DecisionConsequence("treatment", FactString("paracetamol"))
          ),
          DecisionRule(
            List(
              DecisionCondition("symptom", Eq, FactString("headache")),
              DecisionCondition("age", Eq, FactNone()),
              DecisionCondition("weight", Eq, FactNone()),
              DecisionCondition("known", Eq, FactBool(false))
            ),
            DecisionConsequence("treatment", FactNone())
          )
        )

        val actual = VariableForm.fromRules(rules)
        val expected = List(
          VariableForm("symptom", "string"),
          VariableForm("age", "number"),
          VariableForm("weight", "number"),
          VariableForm("known", "bool"),
        )

        actual should equal (expected)
      }
    }
  }
}
