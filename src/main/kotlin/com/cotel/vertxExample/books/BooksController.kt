package com.cotel.vertxExample.books

import arrow.core.Try
import arrow.core.recover
import com.cotel.vertxExample.base.Controller
import com.cotel.vertxExample.base.bodyAsJson
import com.cotel.vertxExample.books.model.AddBookRequest
import com.cotel.vertxExample.books.model.AuthorIsEmpty
import com.cotel.vertxExample.books.model.StorageError
import com.cotel.vertxExample.books.model.TitleIsEmpty
import com.cotel.vertxExample.books.usecases.AddBook
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.koin.standalone.inject

class BooksController(router: Router) : Controller {

  private val getAllBooks: GetAllBooks by inject()
  private val getBookById: GetBookById by inject()
  private val addBook: AddBook by inject()

  init {
    with(router) {
      get("/books").handler(::handleIndex)
      post("/books").handler(::handleCreate)
      get("/books/:id").handler(::handleDetail)
    }
  }

  private fun handleIndex(context: RoutingContext) {
    GlobalScope.launch(context.vertx().dispatcher()) {
      val response = context.response()

      with(response) {
        val books = getAllBooks.execute()

        endWithJson(books)
      }
    }
  }

  private fun handleDetail(context: RoutingContext) {
    GlobalScope.launch(context.vertx().dispatcher()) {
      val response = context.response()

      with(response) {
        Try {
          val id = context.pathParam("id").toLong()

          getBookById.execute(id).fold(
            { endWithError("Book not found with id $id", HttpResponseStatus.NOT_FOUND) },
            { endWithJson(it) }
          )
        }
          .recover { endWithError("Param id must be a number") }
      }
    }
  }

  private fun handleCreate(context: RoutingContext) {
    GlobalScope.launch(context.vertx().dispatcher()) {
      val response = context.response()

      with(response) {
        Try {
          val addBookRequest = context.bodyAsJson<AddBookRequest>()
          addBook.execute(addBookRequest.toDomain())
            .fold(
              {
                when (it) {
                  is TitleIsEmpty -> endWithError("Book title cannot be empty")
                  is AuthorIsEmpty -> endWithError("Author name cannot be empty")
                  is StorageError -> endWithError(
                    "Something failed while adding new item",
                    HttpResponseStatus.INTERNAL_SERVER_ERROR
                  )
                }
              },
              { endWithJson(it, HttpResponseStatus.CREATED) }
            )
        }

          .recover { endWithError("Could not parse request body") }
      }
    }
  }

}
