package com.cotel.vertxExample.books

import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class BooksController(
  private val getAllBooks: GetAllBooks,
  private val getBookById: GetBookById,
  router: Router
) {

  init {
    with(router) {
      route(HttpMethod.GET, "/books").handler(::handleIndex)
      route(HttpMethod.GET, "/books/:id").handler(::handleDetail)
    }
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

  fun handleDetail(context: RoutingContext) {
    GlobalScope.launch {
      val request = context.request()
      val response = context.response()

      with(response) {
        putHeader("content-type", "text/plain; charset=utf-8")
        try {
          val id = request.getParam("id").toLong()

          val book = getBookById.execute(id)

          if (book != null) {
            putHeader("content-type", "application/json; charset=utf-8")
            end(Json.encodePrettily(book))
          } else {
            statusCode = 404
            end("No Book was found with id $id")
          }

        } catch (ex: NumberFormatException) {
          statusCode = 400
          end("Param id must be a number")
        }
      }
    }
  }

}
