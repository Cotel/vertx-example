package com.cotel.vertxExample.books.storage

import arrow.Kind
import arrow.core.Option
import arrow.effects.IO
import arrow.typeclasses.ApplicativeError
import com.cotel.vertxExample.books.model.Book
import java.util.concurrent.atomic.AtomicLong

class BooksDTO<F>(AE: ApplicativeError<F, Throwable>) : ApplicativeError<F, Throwable> by AE {

  private val lastId = AtomicLong(3)
  private val books = mutableListOf(
    Book(1, "The Last Wish", "Andrzej Sapkowski"),
    Book(2, "A Song of Ice and Fire: Game of Thrones", "George R. R. Martin"),
    Book(3, "The Hobbit", "J. R. R. Tolkien")
  )

  fun getAllBooks(): Kind<F, List<Book>> = just(books)

  fun getBookById(id: Long): Kind<F, Option<Book>> =
    just(Option.fromNullable(books.find { it.id == id }))


  fun addBook(book: Book): Kind<F, Book> {
    val newId = lastId.incrementAndGet()
    books.add(book.copy(id = newId))
    return just(book)
  }

}
