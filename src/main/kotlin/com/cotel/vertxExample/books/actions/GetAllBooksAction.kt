package com.cotel.vertxExample.books.actions

import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.fix
import com.cotel.vertxExample.base.jsonResponder
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.queries.GetAllBooks
import com.cotel.vertxExample.books.queries.GetAllBooksQuery
import com.cotel.vertxExample.books.queries.GetAllBooksUseCase
import com.cotel.vertxExample.books.storage.BooksDTO
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class GetAllBooksAction(private val dto: BooksDTO<ForIO>) : Handler<RoutingContext> {
  private fun execute(): IO<List<Book>> = object : GetAllBooksUseCase<ForIO> {
    override val query: GetAllBooks<ForIO> = dto::getAllBooks
  }.run {
    GetAllBooksQuery().runUsecase().fix()
  }

  override fun handle(context: RoutingContext) {
    GlobalScope.launch {
      val respondJson = jsonResponder<List<Book>>(context.response())

      respondJson(execute().unsafeRunSync())
    }
  }
}
