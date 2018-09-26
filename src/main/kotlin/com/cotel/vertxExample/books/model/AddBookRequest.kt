package com.cotel.vertxExample.books.model

data class AddBookRequest(
  val title: String = "",
  val authorName: String = ""
) {
  fun toDomain(): Book = Book(0, title, authorName)
}
