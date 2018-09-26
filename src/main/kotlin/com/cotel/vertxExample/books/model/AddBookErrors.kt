package com.cotel.vertxExample.books.model

sealed class AddBookErrors
object StorageError : AddBookErrors()
object TitleIsEmpty : AddBookErrors()
object AuthorIsEmpty : AddBookErrors()
