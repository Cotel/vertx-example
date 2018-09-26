package com.cotel.vertxExample.books.usecases

import arrow.core.Either
import arrow.core.Left
import arrow.core.right
import com.cotel.vertxExample.books.model.AddBookErrors
import com.cotel.vertxExample.books.model.AuthorIsEmpty
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.model.TitleIsEmpty
import com.cotel.vertxExample.books.storage.BooksDTO
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AddBookTest {

  private val dto = mockk<BooksDTO>(relaxed = true)

  @Test
  fun `should return error if book title is empty`() {
    val addBook = AddBook(dto)

    every { dto.addBook(any()) } returns Book().right()

    val result = addBook.execute(Book())

    assertTrue(result.isLeft())
    assertTrue((result as Either.Left<AddBookErrors, Book>).a == TitleIsEmpty)
  }

  @Test
  fun `should return error if author name is empty`() {
    val addBook = AddBook(dto)

    every { dto.addBook(any()) } returns Book().right()

    val result = addBook.execute(Book(title = "Test"))

    assertTrue(result.isLeft())
    assertTrue((result as Either.Left<AddBookErrors, Book>).a == AuthorIsEmpty)
  }

  @Test
  fun `should return book if correct`() {
    val addBook = AddBook(dto)

    every { dto.addBook(any()) } returns Book().right()

    val result = addBook.execute(Book(title = "Juan", authorName = "Palomo"))

    assertTrue(result.isRight())
  }

}
