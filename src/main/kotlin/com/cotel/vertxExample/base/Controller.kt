package com.cotel.vertxExample.base

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import org.koin.standalone.KoinComponent

interface Controller : KoinComponent {
  fun <A> HttpServerResponse.endWithJson(
    entity: A,
    statusCode: HttpResponseStatus = HttpResponseStatus.OK
  ) {
    putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(statusCode.code())
      .end(Json.encodePrettily(entity))
  }

  fun HttpServerResponse.endWithError(
    msg: String,
    statusCode: HttpResponseStatus = HttpResponseStatus.BAD_REQUEST
  ) {
    putHeader("content-type", "text/plain; charset=utf-8")
      .setStatusCode(statusCode.code())
      .end(msg)
  }
}

inline fun <reified A> RoutingContext.bodyAsJson(): A {
  return Json.decodeValue(getBodyAsString("UTF-8"), A::class.java)
}
