package com.cotel.vertxExample.match.storage

import arrow.Kind
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.effects.typeclasses.Async
import com.cotel.vertxExample.base.json
import com.cotel.vertxExample.match.model.Match
import com.cotel.vertxExample.players.model.Player
import io.vertx.ext.jdbc.JDBCClient

class MatchDAO<F>(
  private val dbClient: JDBCClient,
  A : Async<F>
) : Async<F> by A {

  fun findMatchById(id: String): Kind<F, Option<Match>> = async { callback ->
    //language=PostgreSQL
    dbClient.queryWithParams("""
      SELECT player.name, player.id, match.starting_date, match.ending_date
      FROM player
      inner join match_player on match_player.player_id = player.id
      join match on match_player.match_id = match.id
      where match_player.match_id = ?
    """.trimIndent(), json { listOf(id) }) { result ->
      if (result.failed()) {
        callback(Exception(result.cause()).left())
      } else {
        val resultSet = result.result()
        if (resultSet.rows.isEmpty()) {
          callback(Option.empty<Match>().right())
        } else {
          //language=String
          val players = resultSet.rows.map { Player(it.getString("id"), it.getString("name")) }
          callback(Match(
            id,
            resultSet.rows.first().getLong("starting_date"),
            resultSet.rows.first().getLong("ending_date"),
            players,
            emptyList()
          ).some().right())
        }
      }

    }

  }

}
