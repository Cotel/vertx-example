package com.cotel.vertxExample.books.usecases

import arrow.core.Option
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.storage.BooksDTO

class GetBookById(private val dto: BooksDTO) {
  fun execute(id: Long): Option<Book> = dto.getBookById(id)
}
