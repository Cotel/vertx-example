package com.cotel.vertxExample

import arrow.effects.IO
import io.vertx.ext.web.RoutingContext

abstract class Action<A> {
  fun handle(context: RoutingContext) {
    responder(context).run { execute().unsafeRunSync().respond() }
  }

  protected abstract fun responder(context: RoutingContext): Responder<A>
  protected abstract fun execute(): IO<A>
}
