package com.cotel.vertxExample

import com.cotel.vertxExample.books.BooksController
import com.cotel.vertxExample.books.storage.BooksDTO
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    val booksDTO = BooksDTO()
    val getAllBooks = { GetAllBooks(booksDTO) }
    val getBookById = { GetBookById(booksDTO) }

    val helloWorldController = HelloWorldController(router)
    val booksController = BooksController(getAllBooks(), getBookById(), router)

    server.requestHandler { router.accept(it) }.listen(8080)

    startFuture.complete()
  }

}
