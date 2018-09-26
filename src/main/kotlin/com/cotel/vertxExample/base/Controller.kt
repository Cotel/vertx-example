package com.cotel.vertxExample.base

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json

interface Controller {
  fun <A> HttpServerResponse.successResponse(
    entity: A,
    statusCode: HttpResponseStatus = HttpResponseStatus.ACCEPTED
  ) {
    putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(statusCode.code())
      .end(Json.encodePrettily(entity))
  }

  fun HttpServerResponse.errorResponse(
    msg: String,
    statusCode: HttpResponseStatus = HttpResponseStatus.BAD_REQUEST
  ) {
    putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(statusCode.code())
      .end(msg)
  }
}
