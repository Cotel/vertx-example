package com.cotel.vertxExample

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class HelloWorldController(router: Router) {

  init {
    router.route(HttpMethod.GET, "/hello").handler(::handleIndex)
  }

  private fun handleIndex(context: RoutingContext) {
    val response = context.response()

    with (response) {
      putHeader("content-type", "text/plain")
      end("Hello world from Vertx!")
    }
  }

}
