package com.cotel.vertxExample.match.model

import com.cotel.vertxExample.players.model.Player

data class Match(
  val id: String,
  val startingDate: Long,
  val endingDate: Long,
  val participants: List<Player>,
  val rounds: List<Round>
)
