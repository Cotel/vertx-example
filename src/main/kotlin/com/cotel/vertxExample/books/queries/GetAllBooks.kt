package com.cotel.vertxExample.books.queries

import arrow.Kind
import com.cotel.vertxExample.books.storage.BooksDTO
import com.cotel.vertxExample.books.model.Book

typealias GetAllBooks<F> = () -> Kind<F, List<Book>>

class GetAllBooksQuery

interface GetAllBooksUseCase<F> {
  val query: GetAllBooks<F>

  fun GetAllBooksQuery.runUsecase(): Kind<F, List<Book>> = query()
}
