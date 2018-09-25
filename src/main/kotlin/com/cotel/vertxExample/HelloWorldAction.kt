package com.cotel.vertxExample

import arrow.effects.IO
import arrow.typeclasses.Show
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext

class HelloWorldAction : Action<String>() {

  override fun responder(context: RoutingContext): Responder<String> = object : PlainTextResponderInstance<String> {
    override fun S(): Show<String> = Show.invoke { this }
    override fun response(): HttpServerResponse = context.response()
  }

  override fun execute(): IO<String> =
    IO.just("Hello world from Vertx!")

}
