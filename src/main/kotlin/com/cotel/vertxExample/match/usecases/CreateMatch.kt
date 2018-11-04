package com.cotel.vertxExample.match.usecases

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.data.k
import arrow.data.sequence
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding
import com.cotel.vertxExample.match.model.CreateMatchRequest
import com.cotel.vertxExample.match.model.Match
import com.cotel.vertxExample.match.storage.MatchDAO
import com.cotel.vertxExample.players.storage.PlayersDAO

class CreateMatch<F>(
  private val dao: MatchDAO<F>,
  private val playersDAO: PlayersDAO<F>,
  ME: MonadError<F, Throwable>
) : MonadError<F, Throwable> by ME {

  sealed class Errors {
    object PersistenceError : Errors()
    class InexistentPlayer(val id: String) : Errors()
  }

  fun execute(createMatchRequest: CreateMatchRequest): Kind<F, Either<Errors, Match>> = binding {
    val playersExistence = createMatchRequest.players.map { id ->
      doesPlayerExist(id).map { id toT it }
    }.k().sequence(this@CreateMatch).bind()

    if (playersExistence.any { (_, doesExist) -> !doesExist }) {
      val nonExistingPlayerId = playersExistence.first { !it.b }.a
      Errors.InexistentPlayer(nonExistingPlayerId).left()
    } else {
      dao.createMatch(createMatchRequest).bind().right()
    }
  }.handleError { Errors.PersistenceError.left() }

  private fun doesPlayerExist(playerId: String): Kind<F, Boolean> = playersDAO.findUserById(playerId)
    .map { it.isDefined() }
}
