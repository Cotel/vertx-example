package com.cotel.vertxExample.books.usecases

import com.cotel.vertxExample.books.storage.BooksDTO
import com.cotel.vertxExample.books.model.Book

class GetAllBooks(private val dto: BooksDTO) {
  fun execute(): List<Book> = dto.getAllBooks()
}
