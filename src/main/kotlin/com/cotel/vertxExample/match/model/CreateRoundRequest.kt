package com.cotel.vertxExample.match.model

data class CreateRoundRequest(
  val leftPlayerId: String = "",
  val rightPlayerId: String = "",
  val leftScore: Int = 0,
  val rightScore: Int = 0,
  val draw: Boolean = false,
  val leftBollo: Boolean = false,
  val rightBollo: Boolean = false
)
