package com.cotel.vertxExample.match.usecases

import arrow.Kind
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.typeclasses.MonadError
import arrow.typeclasses.binding
import com.cotel.vertxExample.match.model.CreateRoundRequest
import com.cotel.vertxExample.match.model.Round
import com.cotel.vertxExample.match.storage.MatchDAO
import com.cotel.vertxExample.match.storage.RoundDAO
import com.cotel.vertxExample.players.storage.PlayersDAO

class CreateRound<F>(
  private val matchDAO: MatchDAO<F>,
  private val playersDAO: PlayersDAO<F>,
  private val roundDAO: RoundDAO<F>,
  ME: MonadError<F, Throwable>
) : MonadError<F, Throwable> by ME {
  sealed class Errors {
    class PlayerNotFound(val id: String) : Errors()
    class MatchNotFound(val id: String) : Errors()
    object InvalidDraw : Errors()
    object InvalidScore : Errors()
    object MoreThanOneBolloOrDraw : Errors()
    object PersistenceError : Errors()
  }

  fun execute(matchId: String, createRoundRequest: CreateRoundRequest): Kind<F, Either<Errors, Round>> = binding {
    if (!isValidDraw(createRoundRequest)) Errors.InvalidDraw.left()
    if (!areScoresValid(createRoundRequest)) Errors.InvalidScore.left()
    if (!thereIsOnlyOneBolloOrDraw(createRoundRequest)) Errors.MoreThanOneBolloOrDraw.left()

    val match = matchDAO.findMatchById(matchId).bind()
    if (match.isEmpty()) Errors.MatchNotFound(matchId).left()

    val fetchPlayer = { playerId: String ->
      playersDAO.findUserById(playerId).map { it.toEither { Errors.PlayerNotFound(playerId) } }
    }
    val leftPlayer = fetchPlayer(createRoundRequest.leftPlayerId).bind()
    val rightPlayer = fetchPlayer(createRoundRequest.rightPlayerId).bind()

    val roundId = roundDAO.createRound(matchId, createRoundRequest).bind()

    leftPlayer.flatMap { leftPlayer ->
      rightPlayer.flatMap { rightPlayer ->
        Round(
          roundId,
          matchId,
          leftPlayer,
          rightPlayer,
          createRoundRequest.leftScore,
          createRoundRequest.rightScore,
          createRoundRequest.draw,
          createRoundRequest.leftBollo,
          createRoundRequest.rightBollo
        ).right()
      }
    }
  }.handleError { Errors.PersistenceError.left() }

  private fun isValidDraw(createRoundRequest: CreateRoundRequest): Boolean =
    if (createRoundRequest.draw) {
      createRoundRequest.leftScore == 4 && createRoundRequest.rightScore == 4
    } else true

  private fun areScoresValid(createRoundRequest: CreateRoundRequest): Boolean {
    val isValidScore = { x: Int -> x in 0..5 }
    return isValidScore(createRoundRequest.leftScore) && isValidScore(createRoundRequest.rightScore)
  }

  private fun thereIsOnlyOneBolloOrDraw(createRoundRequest: CreateRoundRequest): Boolean =
    listOf(createRoundRequest.draw, createRoundRequest.leftBollo, createRoundRequest.rightBollo)
      .takeWhile { it }
      .size <= 1

}
