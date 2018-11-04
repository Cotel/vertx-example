package com.cotel.vertxExample.match.model

import com.cotel.vertxExample.players.model.Player

data class Round(
  val id: String,
  val matchId: String,
  val leftPlayer: Player,
  val rightPlayer: Player,
  val leftScore: Int,
  val rightScore: Int,
  val isDraw: Boolean = false,
  val isLeftBollo: Boolean = false,
  val isRightBollo: Boolean = false
)
