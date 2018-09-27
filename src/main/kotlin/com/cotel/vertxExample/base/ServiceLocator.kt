package com.cotel.vertxExample.base

import com.cotel.vertxExample.books.storage.BooksDAO
import com.cotel.vertxExample.books.usecases.AddBook
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import org.koin.dsl.module.module

val mainModule = module {

  single { BooksDAO() }

  factory { GetAllBooks(get()) }
  factory { GetBookById(get()) }
  factory { AddBook(get()) }

}
