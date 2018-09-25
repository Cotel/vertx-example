package com.cotel.vertxExample

import arrow.typeclasses.Show
import io.vertx.core.http.HttpServerResponse

interface Responder<A> {
  fun A.respond()
}

interface PlainTextResponderInstance<A> : Responder<A> {
  fun S(): Show<A>
  fun response(): HttpServerResponse

  override fun A.respond() {
    response().putHeader("content-type", "text/plain")
      .end(S().run { show() })
  }
}
