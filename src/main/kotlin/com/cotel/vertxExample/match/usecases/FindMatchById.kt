package com.cotel.vertxExample.match.usecases

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.typeclasses.ApplicativeError
import com.cotel.vertxExample.match.model.Match
import com.cotel.vertxExample.match.storage.MatchDAO

class FindMatchById<F>(
  private val dao: MatchDAO<F>,
  AE: ApplicativeError<F, Throwable>
) : ApplicativeError<F, Throwable> by AE {

  sealed class Errors {
    object PersistenceError : Errors()
    object MatchNotFound : Errors()
  }

  fun execute(id: String): Kind<F, Either<Errors, Match>> = dao.findMatchById(id)
    .map { it.toEither { Errors.MatchNotFound } }
    .handleError { Errors.PersistenceError.left() }

}
