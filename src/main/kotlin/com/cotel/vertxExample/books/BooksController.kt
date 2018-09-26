package com.cotel.vertxExample.books

import arrow.core.Try
import arrow.core.recover
import com.cotel.vertxExample.base.Controller
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class BooksController(
  private val getAllBooks: GetAllBooks,
  private val getBookById: GetBookById,
  router: Router
) : Controller {

  init {
    with(router) {
      route(HttpMethod.GET, "/books").handler(::handleIndex)
      route(HttpMethod.GET, "/books/:id").handler(::handleDetail)
    }
  }

  private fun handleIndex(context: RoutingContext) {
    GlobalScope.launch {
      val response = context.response()

      with(response) {
        val books = getAllBooks.execute()

        successResponse(books)
      }
    }
  }

  private fun handleDetail(context: RoutingContext) {
    GlobalScope.launch {
      val request = context.request()
      val response = context.response()

      with(response) {
        Try {
          val id = request.getParam("id").toLong()

          getBookById.execute(id).fold(
            { errorResponse("Book not found with id $id", HttpResponseStatus.NOT_FOUND) },
            { successResponse(it) }
          )
        }
          .recover { errorResponse("Param id must be a number") }
      }
    }
  }

}
