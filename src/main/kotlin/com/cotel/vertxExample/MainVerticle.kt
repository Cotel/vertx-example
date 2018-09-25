package com.cotel.vertxExample

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    val helloWorldController = HelloWorldController(router)

    server.requestHandler { router.accept(it) }.listen(8080)
  }

}
