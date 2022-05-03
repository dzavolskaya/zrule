package zrule.engine
package core

import java.time.LocalDateTime

import io.circe.generic.JsonCodec

@JsonCodec
case class DecisionDeployment(
   id: Int,
   name: String,
   tenant: String,
   version: Int,
   date: LocalDateTime,
   code: String
 )
