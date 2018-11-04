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

  fun HttpServerResponse.endWithBadRequestError(msg: String) =
    endWithError(msg, HttpResponseStatus.BAD_REQUEST)

  fun HttpServerResponse.endWithInternalServerError(msg: String) =
    endWithError(msg, HttpResponseStatus.INTERNAL_SERVER_ERROR)

  fun HttpServerResponse.endWithNotFoundError(msg: String) =
    endWithError(msg, HttpResponseStatus.NOT_FOUND)
}

inline fun <reified A> RoutingContext.bodyAsJson(): A {
  return Json.decodeValue(getBodyAsString("UTF-8"), A::class.java)
}
