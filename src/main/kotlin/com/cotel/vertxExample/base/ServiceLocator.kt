package com.cotel.vertxExample.base

import com.cotel.vertxExample.books.storage.BooksDTO
import com.cotel.vertxExample.books.usecases.AddBook
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import org.koin.dsl.module.module

val mainModule = module {

  single { BooksDTO() }

  factory { GetAllBooks(get()) }
  factory { GetBookById(get()) }
  factory { AddBook(get()) }

}
