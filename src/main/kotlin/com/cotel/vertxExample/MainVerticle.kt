package com.cotel.vertxExample

import arrow.effects.IO
import arrow.effects.applicativeError
import com.cotel.vertxExample.books.actions.GetAllBooksAction
import com.cotel.vertxExample.books.actions.GetBookByIdAction
import com.cotel.vertxExample.books.storage.BooksDTO
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router

class MainVerticle : AbstractVerticle() {

  override fun start(startFuture: Future<Void>) {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)

    val booksDTO = BooksDTO(IO.applicativeError())

    val getAllBooksAction = GetAllBooksAction(booksDTO)
    val getBookByIdAction = GetBookByIdAction(booksDTO)

    with(router) {
      route(HttpMethod.GET, "/books").handler(getAllBooksAction::handle)
      route(HttpMethod.GET, "/books/:id").handler(getBookByIdAction::handle)
    }

    server.requestHandler { router.accept(it) }.listen(8080)

    startFuture.complete()
  }

}
