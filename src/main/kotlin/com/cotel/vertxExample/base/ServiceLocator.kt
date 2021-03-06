package com.cotel.vertxExample.base

import arrow.effects.DeferredK
import arrow.effects.ForDeferredK
import arrow.effects.applicativeError
import arrow.effects.async
import arrow.effects.monadDefer
import arrow.effects.monadError
import com.cotel.vertxExample.books.storage.BooksDAO
import com.cotel.vertxExample.books.usecases.AddBook
import com.cotel.vertxExample.books.usecases.GetAllBooks
import com.cotel.vertxExample.books.usecases.GetBookById
import com.cotel.vertxExample.match.storage.MatchDAO
import com.cotel.vertxExample.match.storage.RoundDAO
import com.cotel.vertxExample.match.usecases.CreateMatch
import com.cotel.vertxExample.match.usecases.CreateRound
import com.cotel.vertxExample.match.usecases.FindMatchById
import com.cotel.vertxExample.match.usecases.FinishMatch
import com.cotel.vertxExample.players.storage.PlayersDAO
import com.cotel.vertxExample.players.usecases.FindPlayerById
import com.cotel.vertxExample.players.usecases.GetAllPlayers
import io.vertx.core.Vertx
import io.vertx.ext.jdbc.JDBCClient
import org.koin.dsl.module.module

val mainModule = { vertx: Vertx ->
  module {

    val dbClient: JDBCClient = DatabaseClientFactory.createClient(vertx)
    single { dbClient }

    module("persistence") {
      single { PlayersDAO<ForDeferredK>(get(), DeferredK.async()) }
      single { MatchDAO<ForDeferredK>(get(), DeferredK.async()) }
      single { RoundDAO<ForDeferredK>(get(), DeferredK.async()) }

      module("players") {
        factory { FindPlayerById<ForDeferredK>(get(), DeferredK.applicativeError()) }
        factory { GetAllPlayers<ForDeferredK>(get(), DeferredK.monadDefer()) }
      }

      module("matches") {
        factory { FindMatchById<ForDeferredK>(get(), DeferredK.applicativeError()) }
        factory { CreateMatch<ForDeferredK>(get(), get(), DeferredK.monadError()) }
        factory { CreateRound<ForDeferredK>(get(), get(), get(), DeferredK.monadError()) }
        factory { FinishMatch<ForDeferredK>(get(), DeferredK.monadDefer()) }
      }
    }
  }
}

val booksModule = module {
  single { BooksDAO() }

  factory { GetAllBooks(get()) }
  factory { GetBookById(get()) }
  factory { AddBook(get()) }
}
