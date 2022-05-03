import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

import zrule.engine.core.{FactBool, FactNone, FactNumber, FactString}

class FactValueTest extends AnyFunSpec {

  describe("Two facts number having the same value") {
    val f1 = FactNumber(56.5)
    val f2 = FactNumber(56.5)

    describe("when checking equality") {
      it("should return true") {
        (f1 === f2) should be (Some(true))
      }
    }
    describe("when checking inequality") {
      it("should return false") {
        (f1 !== f2) should be (Some(false))
      }
    }
    describe("when checking if lower") {
      it("should return false") {
        (f1 < f2) should be (Some(false))
      }
    }
    describe("when checking if greater") {
      it("should return false") {
        (f1 > f2) should be (Some(false))
      }
    }
    describe("when checking if lower than") {
      it("should return true") {
        (f1 <= f2) should be (Some(true))
      }
    }
    describe("when checking if greater than") {
      it("should return true") {
        (f1 >= f2) should be (Some(true))
      }
    }
  }

  describe("Two fact numbers f1 and f2 where f1 is lower than f2") {
    val f1 = FactNumber(-56.5)
    val f2 = FactNumber(56.5)

    describe("when checking equality") {
      it("should return false") {
        (f1 === f2) should be (Some(false))
      }
    }
    describe("when checking inequality") {
      it("should return true") {
        (f1 !== f2) should be (Some(true))
      }
    }
    describe("when checking if lower") {
      it("should return true") {
        (f1 < f2) should be (Some(true))
      }
    }
    describe("when checking if greater") {
      it("should return false") {
        (f1 > f2) should be (Some(false))
      }
    }
    describe("when checking if lower than") {
      it("should return true") {
        (f1 <= f2) should be (Some(true))
      }
    }
    describe("when checking if greater than") {
      it("should return false") {
        (f1 >= f2) should be (Some(false))
      }
    }
  }

  describe("Two facts string having the same value") {
    val f1 = FactString("foo")
    val f2 = FactString("foo")

    describe("when checking equality") {
      it("should return true") {
        (f1 === f2) should be (Some(true))
      }
    }
    describe("when checking inequality") {
      it("should return false") {
        (f1 !== f2) should be (Some(false))
      }
    }
    describe("when checking if lower") {
      it("should return none") {
        (f1 < f2) should be (None)
      }
    }
    describe("when checking if greater") {
      it("should return non") {
        (f1 > f2) should be (None)
      }
    }
    describe("when checking if lower than") {
      it("should return none") {
        (f1 <= f2) should be (None)
      }
    }
    describe("when checking if greater than") {
      it("should return none") {
        (f1 >= f2) should be (None)
      }
    }
  }

  describe("Two facts having different types") {
    val f1 = FactString("foo")
    val f2 = FactNumber(1)

    describe("when checking equality") {
      it("should return none") {
        (f1 === f2) should be (None)
      }
    }
    describe("when checking inequality") {
      it("should return none") {
        (f1 !== f2) should be (None)
      }
    }
    describe("when checking if lower") {
      it("should return none") {
        (f1 < f2) should be (None)
      }
    }
    describe("when checking if greater") {
      it("should return none") {
        (f1 > f2) should be (None)
      }
    }
    describe("when checking if lower than") {
      it("should return none") {
        (f1 <= f2) should be (None)
      }
    }
    describe("when checking if greater than") {
      it("should return none") {
        (f1 >= f2) should be (None)
      }
    }
  }

  describe("One string fact and one none fact") {
    val f1 = FactString("foo")
    val f2 = FactNone()

    describe("when checking equality") {
      it("should return false") {
        (f1 === f2) shouldBe Some(false)
      }
    }

    describe("when checking inequality") {
      it("should return true") {
        (f1 !== f2) shouldBe Some(true)
      }
    }

    describe("when checking lower than or equal") {
      it("should return false") {
        (f1 <= f2) shouldBe Some(false)
      }
    }

    describe("when checking lower than") {
      it("should return false") {
        (f1 < f2) shouldBe Some(false)
      }
    }

    describe("when checking greater than") {
      it("should return false") {
        (f1 > f2) shouldBe Some(false)
      }
    }

    describe("when checking greater than or equal") {
      it("should return false") {
        (f1 >= f2) shouldBe Some(false)
      }
    }

  }

  describe("One number fact and one none fact") {
    val f1 = FactNumber(56.5)
    val f2 = FactNone()

    describe("when checking equality") {
      it("should return false") {
        (f1 === f2) shouldBe Some(false)
      }
    }

    describe("when checking inequality") {
      it("should return true") {
        (f1 !== f2) shouldBe Some(true)
      }
    }

    describe("when checking lower than") {
      it("should return false") {
        (f1 < f2) shouldBe Some(false)
      }
    }

    describe("when checking greater than") {
      it("should return false") {
        (f1 > f2) shouldBe Some(false)
      }
    }
  }

  describe("One boolean fact and one none fact") {
    val f1 = FactBool(true)
    val f2 = FactNone()

    describe("when checking equality") {
      it("should return false") {
        (f1 === f2) shouldBe Some(false)
      }
    }

    describe("when checking inequality") {
      it("should return true") {
        (f1 !== f2) shouldBe Some(true)
      }
    }
  }
}
