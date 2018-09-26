package com.cotel.vertxExample.base

import com.cotel.vertxExample.books.storage.BooksDTO
import com.cotel.vertxExample.books.usecases.AddBook
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById

object ServiceLocator {

  val mutableMap: MutableMap<String, Any> = mutableMapOf()

  inline fun <reified T> bind(t: T) {
    mutableMap[T::class.java.name] = t as Any
  }

  inline fun <reified T> factory(fn: () -> T) {
    mutableMap[T::class.java.name] = fn() as Any
  }

  inline fun <reified T> retrieve(): T =
    mutableMap[T::class.java.name] as T

  fun prepare() {
    bind(BooksDTO())

    factory { GetAllBooks(retrieve()) }
    factory { GetBookById(retrieve()) }
    factory { AddBook(retrieve()) }
  }

}
