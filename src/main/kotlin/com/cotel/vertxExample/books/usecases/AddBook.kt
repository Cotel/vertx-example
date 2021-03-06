package com.cotel.vertxExample.books.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.cotel.vertxExample.books.model.AddBookErrors
import com.cotel.vertxExample.books.model.AuthorIsEmpty
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.model.TitleIsEmpty
import com.cotel.vertxExample.books.storage.BooksDAO

class AddBook(private val DAO: BooksDAO) {
  fun execute(book: Book): Either<AddBookErrors, Book> = validate(book)
    .flatMap { DAO.addBook(it) }

  private fun validate(book: Book): Either<AddBookErrors, Book> = when {
    book.title.isEmpty() -> TitleIsEmpty.left()
    book.authorName.isEmpty() -> AuthorIsEmpty.left()
    else -> book.right()
  }
}
