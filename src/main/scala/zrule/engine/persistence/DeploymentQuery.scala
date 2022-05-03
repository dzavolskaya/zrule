package zrule.engine
package persistence

import doobie._
import doobie.postgres.implicits._
import doobie.implicits._
import core.DecisionDeployment

object DeploymentQuery {

  def createTable: Update0 = {
    sql"""
         |CREATE TABLE IF NOT EXISTS deployment (
         |id SERIAL NOT NULL,
         |PRIMARY KEY(id),
         |name VARCHAR(50) NOT NULL,
         |tenant VARCHAR(50) NOT NULL,
         |version INT NOT NULL,
         |date TIMESTAMP NOT NULL,
         |code TEXT NOT NULL
         |)
       """.stripMargin
      .update
  }

  def insert(d: DecisionDeployment): Update0 = {
    sql"""
         |INSERT INTO deployment (id, name, tenant, version, date, code)
         |VALUES (${d.id}, ${d.name}, ${d.tenant}, ${d.version}, ${d.date}, ${d.code})
       """.stripMargin
      .update
  }

  def getById(id: Int): Query0[DecisionDeployment] = {
    sql"""
         |SELECT id, name, tenant, version, date, code
         |FROM deployment
         |WHERE id = $id
         |LIMIT 1
       """.stripMargin
      .query
  }

  def getByNameAndTenant(name: String, tenant: String, version: Option[Int]): Query0[DecisionDeployment] = {
    val result = version match {
      case Some(version) => sql"""
        |SELECT id, name, tenant, version, date, code
        |FROM deployment
        |WHERE name = $name AND tenant = $tenant AND version = $version
       """
      case None => sql"""
        |SELECT id, name, tenant, version, date, code
        |FROM deployment
        |WHERE name = $name AND tenant = $tenant
        |ORDER BY version DESC
      """
    }

    result.stripMargin.query
  }

  def getLastVersionByNameAndTenant(name: String, tenant: String): Query0[DecisionDeployment] = {
    sql"""
         |SELECT id, name, tenant, version, date, code
         |FROM deployment
         |WHERE name = $name AND tenant = $tenant
         |ORDER BY version DESC
         |LIMIT 1
       ;""".stripMargin
      .query
  }

  // TODO better way - short integer uuid
  def getLatestId: Query0[Int] = {
    sql"""
      |SELECT id
      |FROM deployment
      |ORDER BY id DESC
      |LIMIT 1
     ;""".stripMargin
      .query
  }

}

