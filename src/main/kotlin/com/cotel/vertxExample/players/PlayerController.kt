package com.cotel.vertxExample.players

import arrow.core.Try
import arrow.effects.ForDeferredK
import arrow.effects.fix
import com.cotel.vertxExample.base.Controller
import com.cotel.vertxExample.players.usecases.FindPlayerById
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.inject

class PlayerController(router: Router) : Controller {

  private val findPlayerById: FindPlayerById<ForDeferredK> by inject()

  init {
    with(router) {
      get("/players/:id").handler(::handleDetail)
    }
  }

  private fun handleDetail(context: RoutingContext) {
    GlobalScope.launch(context.vertx().dispatcher()) {
      val response = context.response()

      with(response) {
        Try {
          val id = context.pathParam("id")

          val result = findPlayerById.execute(id).fix().deferred.await()

          result.fold(
            { when (it) {
              is FindPlayerById.Errors.PersistenceError -> endWithInternalServerError("Persistence error")
              is FindPlayerById.Errors.PlayerNotFound -> endWithNotFoundError("Player not found")
            } },
            { endWithJson(it) }
          )
        }
      }
    }
  }
}
