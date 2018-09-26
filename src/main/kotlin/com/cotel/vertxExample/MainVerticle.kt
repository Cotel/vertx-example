package com.cotel.vertxExample

import com.cotel.vertxExample.base.ServiceLocator
import com.cotel.vertxExample.books.BooksController
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
      .apply { route().handler(BodyHandler.create()) }

    ServiceLocator.prepare()

    val helloController = HelloWorldController(router)
    val booksController =
      BooksController(ServiceLocator.retrieve(), ServiceLocator.retrieve(), ServiceLocator.retrieve(), router)

    server.requestHandler { router.accept(it) }.listen(8080)

    startFuture.complete()
  }

}
