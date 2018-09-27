package com.cotel.vertxExample.books.usecases

import com.cotel.vertxExample.books.storage.BooksDAO
import com.cotel.vertxExample.books.model.Book

class GetAllBooks(private val DAO: BooksDAO) {
  fun execute(): List<Book> = DAO.getAllBooks()
}
