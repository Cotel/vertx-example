package com.cotel.vertxExample.base

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json

fun plainTextResponder(response: HttpServerResponse) = { message: String ->
  response.putHeader("content-type", "text/plain; charset=utf-8")
    .end(message)
}

fun <A> jsonResponder(response: HttpServerResponse) = { a: A ->
  response.putHeader("content-type", "application/json; charset=utf-8")
    .end(Json.encodePrettily(a))
}

fun errorResponder(response: HttpServerResponse) = { errorCode: Int, message: String ->
  response.putHeader("content-type", "text/plain; charset=utf-8")
    .setStatusCode(errorCode)
    .end(message)
}
