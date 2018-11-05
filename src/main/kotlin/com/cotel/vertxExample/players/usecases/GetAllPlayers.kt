package com.cotel.vertxExample.players.usecases

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import com.cotel.vertxExample.players.model.Player
import com.cotel.vertxExample.players.storage.PlayersDAO

class GetAllPlayers<F>(
  private val dao: PlayersDAO<F>,
  MD: MonadDefer<F>
) : MonadDefer<F> by MD {
  fun execute(): Kind<F, List<Player>> = dao.getAllPlayers()
}
