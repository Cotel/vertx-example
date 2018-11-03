package com.cotel.vertxExample.base

import arrow.effects.DeferredK
import arrow.effects.ForDeferredK
import arrow.effects.applicativeError
import arrow.effects.async
import com.cotel.vertxExample.books.storage.BooksDAO
import com.cotel.vertxExample.books.usecases.AddBook
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import com.cotel.vertxExample.match.storage.MatchDAO
import com.cotel.vertxExample.match.usecases.FindMatchById
import com.cotel.vertxExample.players.storage.PlayersDAO
import com.cotel.vertxExample.players.usecases.FindPlayerById
import io.vertx.core.Vertx
import io.vertx.ext.jdbc.JDBCClient
import org.koin.dsl.module.module

val mainModule = { vertx: Vertx ->
  module {

    val dbClient: JDBCClient = DatabaseClientFactory.createClient(vertx)
    single { dbClient }

    module("players") {
      single { PlayersDAO<ForDeferredK>(get(), DeferredK.async()) }

      factory { FindPlayerById<ForDeferredK>(get(), DeferredK.applicativeError()) }
    }

    module("matches") {
      single { MatchDAO<ForDeferredK>(get(), DeferredK.async()) }

      factory { FindMatchById<ForDeferredK>(get(), DeferredK.applicativeError()) }
    }
  }
}

val booksModule = module {
  single { BooksDAO() }

  factory { GetAllBooks(get()) }
  factory { GetBookById(get()) }
  factory { AddBook(get()) }
}
