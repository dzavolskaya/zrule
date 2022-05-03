package zrule.engine
package core

import io.circe.generic.JsonCodec

@JsonCodec
case class Decision(
   name: String,
   tenant: String,
   code: String
 )
