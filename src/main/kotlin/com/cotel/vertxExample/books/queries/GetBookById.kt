package com.cotel.vertxExample.books.queries

import arrow.Kind
import arrow.core.Option
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.storage.BooksDTO

typealias GetBookById<F> = (Long) -> Kind<F, Option<Book>>

data class GetBookByIdQuery(val id: Long)

interface GetBookByIdUseCase<F> {

  val query: GetBookById<F>

  fun GetBookByIdQuery.runUsecase(): Kind<F, Option<Book>> = query(id)
}
