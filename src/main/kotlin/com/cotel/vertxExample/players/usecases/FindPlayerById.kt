package com.cotel.vertxExample.players.usecases

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.typeclasses.ApplicativeError
import com.cotel.vertxExample.players.model.Player
import com.cotel.vertxExample.players.storage.PlayersDAO

class FindPlayerById<F>(
  private val dao: PlayersDAO<F>,
  AE: ApplicativeError<F, Throwable>
) : ApplicativeError<F, Throwable> by AE {

  sealed class Errors {
    object PlayerNotFound : Errors()
    object PersistenceError : Errors()
  }

  fun execute(id: String): Kind<F, Either<FindPlayerById.Errors, Player>> = dao.findUserById(id)
    .map { it.toEither { FindPlayerById.Errors.PlayerNotFound } }
    .handleError { FindPlayerById.Errors.PersistenceError.left() }
}
