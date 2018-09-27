package com.cotel.vertxExample.books.storage

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.model.StorageError
import java.util.concurrent.atomic.AtomicLong

class BooksDAO {

  private val lastId = AtomicLong(3)
  private val books = mutableListOf(
    Book(1, "The Last Wish", "Andrzej Sapkowski"),
    Book(2, "A Song of Ice and Fire: Game of Thrones", "George R. R. Martin"),
    Book(3, "The Hobbit", "J. R. R. Tolkien")
  )

  fun getAllBooks(): List<Book> = books

  fun getBookById(id: Long): Option<Book> = Option.fromNullable(books.find { it.id == id })

  fun addBook(book: Book): Either<StorageError, Book> {
    val isRepeated = books.find { it.title.toLowerCase() == book.title.toLowerCase() } != null

    if (isRepeated) return StorageError.left()

    val newId = lastId.incrementAndGet()
    val storedBook = book.copy(id = newId)
    books.add(storedBook)

    return storedBook.right()
  }

}
