package com.cotel.vertxExample.match

import arrow.core.Try
import arrow.core.recover
import arrow.effects.ForDeferredK
import arrow.effects.fix
import arrow.effects.unsafeAttemptSync
import com.cotel.vertxExample.base.Controller
import com.cotel.vertxExample.base.bodyAsJson
import com.cotel.vertxExample.match.model.CreateMatchRequest
import com.cotel.vertxExample.match.model.CreateRoundRequest
import com.cotel.vertxExample.match.usecases.CreateMatch
import com.cotel.vertxExample.match.usecases.CreateRound
import com.cotel.vertxExample.match.usecases.FindMatchById
import com.cotel.vertxExample.match.usecases.FinishMatch
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.inject

class MatchController(router: Router) : Controller {

  private val findMatchById: FindMatchById<ForDeferredK> by inject()
  private val createMatch: CreateMatch<ForDeferredK> by inject()
  private val createRound: CreateRound<ForDeferredK> by inject()
  private val finishMatch: FinishMatch<ForDeferredK> by inject()

  init {
    with(router) {
      get("/matches/:id").handler(::matchDetails)
      post("/matches").handler(::createMatch)
      put("/matches/:id/finish").handler(::finishMatch)
      post("/matches/:id/rounds").handler(::createRoundForMatch)
    }
  }

  private fun matchDetails(routingContext: RoutingContext) {
    GlobalScope.launch(routingContext.vertx().dispatcher()) {
      val id = routingContext.pathParam("id")

      with(routingContext.response()) {
        val result = findMatchById.execute(id).fix().await()

        result.fold(
          {
            when (it) {
              is FindMatchById.Errors.PersistenceError -> endWithInternalServerError("Persitence error")
              is FindMatchById.Errors.MatchNotFound -> endWithNotFoundError("Match not found")
            }
          },
          { endWithJson(it) }
        )
      }
    }
  }

  private fun createMatch(routingContext: RoutingContext) {
    GlobalScope.launch(routingContext.vertx().dispatcher()) {
      with(routingContext.response()) {
        Try {
          val createMatchRequest = routingContext.bodyAsJson<CreateMatchRequest>()

          createMatch.execute(createMatchRequest).fix().await()
            .fold(
              {
                when (it) {
                  is CreateMatch.Errors.PersistenceError -> endWithInternalServerError("Persistence error")
                  is CreateMatch.Errors.InexistentPlayer ->
                    endWithBadRequestError("Player with id ${it.id} does not exist")
                }
              },
              { endWithJson(it) }
            )

        }.recover {
          endWithBadRequestError("Could not parse request body")
        }
      }
    }
  }

  private fun finishMatch(routingContext: RoutingContext) {
    GlobalScope.launch(routingContext.vertx().dispatcher()) {
      with(routingContext.response()) {
        Try {
          val id = routingContext.request().getParam("id")

          finishMatch.execute(id, System.currentTimeMillis()).fix().unsafeAttemptSync()
            .fold(
              {
                when (it) {
                  is FinishMatch.Errors.MatchNotFound -> endWithNotFoundError("Match with id ${it.id} does not exist")
                  else -> endWithInternalServerError("Persistence error")
                }
              },
              { endWithJson(it) }
            )
        }
      }
    }
  }

  private fun createRoundForMatch(routingContext: RoutingContext) {
    GlobalScope.launch(routingContext.vertx().dispatcher()) {
      with(routingContext.response()) {
        Try {
          val matchId = routingContext.request().getParam("id")
          val createRoundRequest = routingContext.bodyAsJson<CreateRoundRequest>()

          createRound.execute(matchId, createRoundRequest).fix().await()
            .fold(
              {
                when (it) {
                  is CreateRound.Errors.PlayerNotFound ->
                    endWithNotFoundError("Player with id ${it.id} was not found")
                  is CreateRound.Errors.MatchNotFound ->
                    endWithNotFoundError("Match with id ${it.id} was not found")
                  is CreateRound.Errors.InvalidDraw ->
                    endWithBadRequestError("If round ends with draw scores must be exactly 4")
                  is CreateRound.Errors.InvalidScore ->
                    endWithBadRequestError("Score cannot must be between 0 and 5")
                  is CreateRound.Errors.MoreThanOneBolloOrDraw ->
                    endWithBadRequestError("There can only be one bollo or draw")
                  is CreateRound.Errors.PersistenceError ->
                    endWithInternalServerError("Persistence error")
                }
              },
              { endWithJson(it) }
            )
        }.recover { endWithBadRequestError("Could not parse request body") }
      }
    }
  }

}
