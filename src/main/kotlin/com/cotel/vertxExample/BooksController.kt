package com.cotel.vertxExample

import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class BooksController(private val getAllBooks: GetAllBooks, router: Router) {

  init {
    router
      .route(HttpMethod.GET, "/books").handler(::handleIndex)
  }

  fun handleIndex(context: RoutingContext) {
    GlobalScope.launch {
      val response = context.response()

      with(response) {
        val books = getAllBooks.execute()

        putHeader("content-type", "application/json; charset=utf-8")
        end(Json.encodePrettily(books))
      }
    }
  }

}
