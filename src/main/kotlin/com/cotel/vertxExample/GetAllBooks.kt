package com.cotel.vertxExample

class GetAllBooks(private val dto: BooksDTO) {
  fun execute(): List<Book> = dto.getAllBooks()
}
