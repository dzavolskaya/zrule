import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Inside._
import org.scalatest.matchers.should.Matchers._

import zrule.engine.core.{DecisionCondition, DecisionConsequence, DecisionRule, FactNone, FactNumber, FactString, Solver, SolverError, Variable}
import zrule.engine.core.ConditionType._
import zrule.engine.core.SolverPolicy._

class SolverTest extends AnyFunSpec {
  describe("A list of rules and a list of variables targeting a unique match") {
    val decisionRules = List(
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Eq, FactString("aspirin"))
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      ),
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Ne, FactString("aspirin"))
        ),
        DecisionConsequence("treatment", FactString("aspirin"))
      )
    )

    val variables = List(
      Variable("symptom", FactString("headache")),
      Variable("age", FactNumber(38)),
      Variable("allergy", FactString("antibiotic"))
    )

    val expected = List(DecisionConsequence("treatment", FactString("aspirin")))

    describe("when solving by applying a first hit policy") {
      it("should return a list of 1 consequence") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, First)

        inside(actual) {
          case Right(l) => l should equal (expected)
        }

      }
    }

    describe("when solving by applying a unique hit policy") {
      it("should return a list of 1 consequence") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, Unique)

        inside(actual) {
          case Right(l) => l should equal (expected)
        }
      }
    }

    describe("when solving by applying all hit policy") {
      it("should return a list of 1 consequence") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, All)

        inside(actual) {
          case Right(l) => l should equal (expected)
        }
      }
    }
  }

  describe("A list of rules and a list of variables targeting duplicated matches") {
    val decisionRules = List(
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      ),
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Lte, FactNumber(85)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      )
    )

    val variables = List(
      Variable("symptom", FactString("headache")),
      Variable("age", FactNumber(38)),
      Variable("allergy", FactNone())
    )

    describe("when solving by applying a first hit policy") {
      it("should return a list of 1 consequence") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, First)
        val expected = List(DecisionConsequence("treatment", FactString("paracetamol")))

        inside(actual) {
          case Right(l) => l should equal (expected)
        }

      }
    }

    describe("when solving by applying a unique hit policy") {
      it("should return an error") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, Unique)

        inside(actual) {
          case Left(l) => l shouldBe a [SolverError]
        }
      }
    }

    describe("when solving by applying all hit policy") {
      it("should return a list of 2 consequences") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, All)
        val expected = List(
          DecisionConsequence("treatment", FactString("paracetamol")),
          DecisionConsequence("treatment", FactString("paracetamol"))
        )
        inside(actual) {
          case Right(l) => l should equal (expected)
        }
      }
    }
  }

  describe("A list of rules and a list of variables targeting multiple distinct matches") {
    val decisionRules = List(
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      ),
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Lte, FactNumber(85)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("aspirin"))
      )
    )

    val variables = List(
      Variable("symptom", FactString("headache")),
      Variable("age", FactNumber(38)),
      Variable("allergy", FactNone())
    )

    describe("when solving by applying a first hit policy") {
      it("should return a list of 1 consequence") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, First)
        val expected = List(DecisionConsequence("treatment", FactString("paracetamol")))

        inside(actual) {
          case Right(l) => l should equal (expected)
        }

      }
    }

    describe("when solving by applying a unique hit policy") {
      it("should return a list of 2 consequences") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, Unique)
        val expected = List(
          DecisionConsequence("treatment", FactString("paracetamol")),
          DecisionConsequence("treatment", FactString("aspirin"))
        )
        inside(actual) {
          case Right(l) => l should equal (expected)
        }
      }
    }

    describe("when solving by applying all hit policy") {
      it("should return a list of 2 consequences") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, All)
        val expected = List(
          DecisionConsequence("treatment", FactString("paracetamol")),
          DecisionConsequence("treatment", FactString("aspirin"))
        )
        inside(actual) {
          case Right(l) => l should equal (expected)
        }
      }
    }
  }

  describe("A list of rules and a list of variables targeting no match") {
    val decisionRules = List(
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      ),
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Lte, FactNumber(85)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("aspirin"))
      )
    )

    val variables = List(
      Variable("symptom", FactString("sore throat")),
      Variable("age", FactNumber(38)),
      Variable("allergy", FactNone())
    )

    describe("when solving by applying a first hit policy") {
      it("should return an empty list") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, First)

        inside(actual) {
          case Right(l) => l shouldBe empty
        }

      }
    }

    describe("when solving by applying a unique hit policy") {
      it("should return an empty list") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, Unique)

        inside(actual) {
          case Right(l) => l shouldBe empty
        }
      }
    }

    describe("when solving by applying all hit policy") {
      it("should return an empty list") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, All)

        inside(actual) {
          case Right(l) => l shouldBe empty
        }
      }
    }
  }

  describe("A list of rules and a list of variables where one is missing and is actually needed") {
    val decisionRules = List(
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      ),
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Lte, FactNumber(85)),
          DecisionCondition("allergy", Eq, FactNone())
        ),
        DecisionConsequence("treatment", FactString("aspirin"))
      )
    )

    val variables = List(
      Variable("symptom", FactString("headache")),
      Variable("allergy", FactNone())
    )

    describe("when solving by applying a first hit policy") {
      it("should return an error") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, First)

        inside(actual) {
          case Left(l) => l shouldBe a [SolverError]
        }

      }
    }

    describe("when solving by applying a unique hit policy") {
      it("should return an error") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, Unique)

        inside(actual) {
          case Left(l) => l shouldBe a [SolverError]
        }

      }
    }

    describe("when solving by applying all hit policy") {
      it("should return an error") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, All)

        inside(actual) {
          case Left(l) => l shouldBe a [SolverError]
        }

      }
    }
  }

  describe("A list of rules and a list of variables for which one is none") {
    val decisionRules = List(
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Eq, FactString("aspirin"))
        ),
        DecisionConsequence("treatment", FactString("paracetamol"))
      ),
      DecisionRule(
        List(
          DecisionCondition("symptom", Eq, FactString("headache")),
          DecisionCondition("age", Gte, FactNumber(15)),
          DecisionCondition("allergy", Ne, FactString("aspirin"))
        ),
        DecisionConsequence("treatment", FactString("aspirin"))
      )
    )

    val variables = List(
      Variable("symptom", FactString("headache")),
      Variable("age", FactNumber(38)),
      Variable("allergy", FactNone())
    )

    val expected = List(DecisionConsequence("treatment", FactString("aspirin")))

    describe("when solving by applying a first hit policy") {
      it("should return a list of 1 consequence") {
        val solver = new Solver(decisionRules)
        val actual = solver.solve(variables, First)

        inside(actual) {
          case Right(l) => l should equal(expected)
        }

      }
    }
  }
}
