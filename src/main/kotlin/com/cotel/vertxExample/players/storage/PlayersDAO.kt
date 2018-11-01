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

  fun findUserById(id: String): Kind<F, Option<Player>> = async { callback ->
    dbClient.queryWithParams("SELECT ID, NAME FROM PLAYER WHERE ID=?", json { listOf(id) }) { result ->
      if (result.failed()) {
        callback(Exception(result.cause()).left())
      } else {
        val resultSet = result.result()
        if (resultSet.rows.size == 1) {
          val player = Player(resultSet.rows[0].getString("ID"), resultSet.rows[0].getString("NAME")).some()
          callback(player.right())
        } else {
          callback(Option.empty<Player>().right())
        }
      }
    }


  }

}
