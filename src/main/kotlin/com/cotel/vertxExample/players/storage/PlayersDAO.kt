package com.cotel.vertxExample.players.storage

import arrow.Kind
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.effects.typeclasses.Async
import com.cotel.vertxExample.base.json
import com.cotel.vertxExample.players.model.Player
import io.vertx.ext.jdbc.JDBCClient

class PlayersDAO<F>(
  private val dbClient: JDBCClient,
  A: Async<F>
) : Async<F> by A {

  init {
    dbClient.getConnection { ar ->
      if (ar.failed()) {
        System.err.println("CONNECTION TO DATABASE FAILED!!!!!")
      } else {
        ar.result().close()
      }
    }
  }

  fun findUserById(id: String): Kind<F, Option<Player>> = async { callback ->
    //language=PostgreSQL
    dbClient.queryWithParams("SELECT id, name FROM player WHERE id=?", json { listOf(id) }) { result ->
      if (result.failed()) {
        System.err.println(result.cause())
        callback(Exception(result.cause()).left())
      } else {
        val resultSet = result.result()
        if (resultSet.rows.size == 1) {
          //language=String
          val player = Player(resultSet.rows[0].getString("id"), resultSet.rows[0].getString("name")).some()
          callback(player.right())
        } else {
          callback(Option.empty<Player>().right())
        }
      }
    }
  }

}
