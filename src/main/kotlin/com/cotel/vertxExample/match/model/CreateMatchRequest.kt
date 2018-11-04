package com.cotel.vertxExample.match.model

data class CreateMatchRequest(
  val startingDate: Long = 0,
  val players: List<String> = emptyList()
)
