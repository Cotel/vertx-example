package com.cotel.vertxExample.match

import arrow.effects.ForDeferredK
import arrow.effects.fix
import com.cotel.vertxExample.base.Controller
import com.cotel.vertxExample.match.usecases.FindMatchById
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.inject

class MatchController(router: Router) : Controller {

  private val findMatchById: FindMatchById<ForDeferredK> by inject()

  init {
    with(router) {
      get("/matches/:id").handler(::matchDetails)
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

}
