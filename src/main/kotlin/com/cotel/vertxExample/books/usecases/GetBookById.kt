package com.cotel.vertxExample.books.usecases

import arrow.core.Option
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.storage.BooksDAO

class GetBookById(private val DAO: BooksDAO) {
  fun execute(id: Long): Option<Book> = DAO.getBookById(id)
}
