package com.cotel.vertxExample.books.actions

import arrow.core.Option
import arrow.core.Try
import arrow.core.recover
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.fix
import com.cotel.vertxExample.base.errorResponder
import com.cotel.vertxExample.base.jsonResponder
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.queries.GetBookById
import com.cotel.vertxExample.books.queries.GetBookByIdQuery
import com.cotel.vertxExample.books.queries.GetBookByIdUseCase
import com.cotel.vertxExample.books.storage.BooksDTO
import com.cotel.vertxExample.books.storage.BooksStorageErrors
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class GetBookByIdAction(private val dto: BooksDTO<ForIO>) : Handler<RoutingContext> {

  private fun execute(id: Long): IO<Option<Book>> = object : GetBookByIdUseCase<ForIO> {
    override val query: GetBookById<ForIO> = dto::getBookById
  }.run {
    GetBookByIdQuery(id).runUsecase().fix()
  }

  override fun handle(event: RoutingContext) {
    GlobalScope.launch {

      val respondError = errorResponder(event.response())
      val respondJson = jsonResponder<Book>(event.response())

      Try {
        val param = event.request().getParam("id").toLong()
        execute(param).unsafeRunSync()
          .fold(
            { respondError(404, BooksStorageErrors.BookNotFound(param).message!!) },
            { respondJson(it) }
          )
      }.recover { respondError(400, "Param id must be a number") }

    }
  }
}
