package com.cotel.vertxExample

import java.util.concurrent.atomic.AtomicLong

class BooksDTO {

  private val lastId = AtomicLong(3)
  private val books = mutableListOf(
    Book(1, "The Last Wish", "Andrzej Sapkowski"),
    Book(2, "A Song of Ice and Fire: Game of Thrones", "George R. R. Martin"),
    Book(3, "The Hobbit", "J. R. R. Tolkien")
  )

  fun getAllBooks(): List<Book> = books

  fun getBookById(id: Long): Book? = books.find { it.id == id }

  fun addBook(book: Book) {
    val newId = lastId.incrementAndGet()
    books.add(book.copy(id = newId))
  }

}
