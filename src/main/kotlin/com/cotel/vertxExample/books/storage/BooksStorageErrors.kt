package com.cotel.vertxExample.books.storage

sealed class BooksStorageErrors(msg: String) : Throwable(msg) {
  class BookNotFound(id: Long) : BooksStorageErrors("No Book was found with id $id")
}
