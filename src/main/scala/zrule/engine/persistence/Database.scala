package zrule.engine
package persistence

import scala.io.AnsiColor._
import scala.util.Try

import cats.effect.{Async, ContextShift}
import doobie.util.transactor.Transactor

case class Database(
  host: String,
  port: Int,
  username: String,
  password: String,
  databaseName: String,
) {
  implicit def transactor[M[_]: Async: ContextShift]: Transactor[M] = {

    println(s"${REVERSED}Using following database config:$RESET")
    println(
      s"""$CYAN
          |Database host: $host
          |Database port: $port
          |Database username: $username
          |Database password: $password
          |Database name: $databaseName$RESET""".stripMargin
    )

    Transactor.fromDriverManager[M](
      "org.postgresql.Driver",
      s"jdbc:postgresql://$host:$port/$databaseName",
      username,
      password,
    )
  }
}

object Database {
  def load(): Try[Database] = Try {

    // TODO - make it load from config file
    val host = "localhost"
    val port = 5432
    val username = "postgres"
    val password = "postgres"
    val databaseName = "postgres"

    Database(host = host, port = port, username = username, password = password, databaseName = databaseName)
  }
}
