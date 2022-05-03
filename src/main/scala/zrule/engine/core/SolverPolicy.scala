package zrule.engine
package core

object SolverPolicy extends Enumeration {
  type SolverPolicy = Value
  val First, All, Unique = Value

  def fromString(value: String): SolverPolicy = value.stripMargin.toLowerCase() match {
    case "first" => First
    case "all" => All
    case "unique" => Unique
  }
}
