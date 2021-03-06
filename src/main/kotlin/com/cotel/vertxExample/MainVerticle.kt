package com.cotel.vertxExample

import com.cotel.vertxExample.books.BooksController
import com.cotel.vertxExample.match.MatchController
import com.cotel.vertxExample.players.PlayerController
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
      .apply { route().handler(BodyHandler.create()) }

    val helloController = HelloWorldController(router)
    val booksController = BooksController(router)
    val playerController = PlayerController(router)
    val matchController = MatchController(router)

    server.requestHandler { router.accept(it) }.listen(8080)

    startFuture.complete()
  }

}
