package com.cotel.vertxExample.match

import arrow.core.Try
import arrow.core.recover
import arrow.effects.ForDeferredK
import arrow.effects.fix
import com.cotel.vertxExample.base.Controller
import com.cotel.vertxExample.base.bodyAsJson
import com.cotel.vertxExample.match.model.CreateMatchRequest
import com.cotel.vertxExample.match.usecases.CreateMatch
import com.cotel.vertxExample.match.usecases.FindMatchById
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.inject

class MatchController(router: Router) : Controller {

  private val findMatchById: FindMatchById<ForDeferredK> by inject()
  private val createMatch: CreateMatch<ForDeferredK> by inject()

  init {
    with(router) {
      get("/matches/:id").handler(::matchDetails)
      post("/matches").handler(::createMatch)
    }
  }

  private fun matchDetails(routingContext: RoutingContext) {
    GlobalScope.launch(routingContext.vertx().dispatcher()) {
      val id = routingContext.pathParam("id")

      with (routingContext.response()) {
        val result = findMatchById.execute(id).fix().await()

        result.fold(
          { when (it) {
            is FindMatchById.Errors.PersistenceError -> endWithInternalServerError("Persitence error")
            is FindMatchById.Errors.MatchNotFound -> endWithNotFoundError("Match not found")
          } },
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

}
