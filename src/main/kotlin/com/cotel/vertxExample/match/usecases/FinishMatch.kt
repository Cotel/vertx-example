package com.cotel.vertxExample.match.usecases

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.binding
import com.cotel.vertxExample.match.model.Match
import com.cotel.vertxExample.match.storage.MatchDAO

class FinishMatch<F>(private val dao: MatchDAO<F>, MD: MonadDefer<F>) : MonadDefer<F> by MD {

  sealed class Errors : Exception() {
    class MatchNotFound(val id: String) : Errors()
  }

  fun execute(matchId: String, currentTime: Long): Kind<F, Match> = binding {
    val match = dao.findMatchById(matchId).bind()
      .fold({ raiseError<Match>(Errors.MatchNotFound(matchId)) }, { just(it) })
      .bind()
    val finishedMatch = match.copy(endingDate = currentTime)
    dao.updateMatch(finishedMatch).bind()
  }
}
