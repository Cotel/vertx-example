package com.cotel.vertxExample.match.storage

import arrow.Kind
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.effects.typeclasses.Async
import com.cotel.vertxExample.base.json
import com.cotel.vertxExample.match.model.CreateMatchRequest
import com.cotel.vertxExample.match.model.Match
import com.cotel.vertxExample.match.model.Round
import com.cotel.vertxExample.players.model.Player
import io.vertx.ext.jdbc.JDBCClient
import java.util.UUID

class MatchDAO<F>(
  private val dbClient: JDBCClient,
  A: Async<F>
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
          val startingDate = resultSet.rows.first().getLong("starting_date")
          val endingDate = resultSet.rows.first().getLong("ending_date")

          //language=PostgreSQL
          dbClient.queryWithParams("""
            SELECT round.id, round.left_player_id, round.right_player_id, round.left_score, round.right_score, round.is_draw, round.is_left_bollo, round.is_right_bollo
            FROM round LEFT JOIN match ON match.id = round.match_id
            WHERE match.id = ?
          """.trimIndent(), json { listOf(id) }) { result ->
            if (result.failed()) callback(Exception(result.cause()).left())
            else {
              val resultSet = result.result()
              //language=String
              val rounds = resultSet.rows.map { row ->
                Round(
                  row.getString("id"),
                  id,
                  players.first { it.id == row.getString("left_player_id") },
                  players.first { it.id == row.getString("right_player_id") },
                  row.getInteger("left_score"),
                  row.getInteger("right_score"),
                  row.getBoolean("is_draw"),
                  row.getBoolean("is_left_bollo"),
                  row.getBoolean("is_right_bollo")
                )
              }

              callback(Match(
                id,
                startingDate,
                endingDate,
                players,
                rounds
              ).some().right())
            }
          }
        }
      }
    }
  }

  fun createMatch(createMatchRequest: CreateMatchRequest): Kind<F, Match> = async { callback ->
    dbClient.getConnection { result ->
      if (result.failed()) callback(Exception(result.cause()).left())
      else {
        val connection = result.result()

        val newUUID = UUID.randomUUID().toString()
        val manyToManyValues = createMatchRequest.players.joinToString(", ") { "('$newUUID', '$it')" }

        //language=PostgreSQL
        connection.execute("""
          BEGIN;

          INSERT INTO match (id, starting_date, ending_date) VALUES ('$newUUID', ${createMatchRequest.startingDate}, 0);

          INSERT INTO match_player (match_id, player_id) VALUES $manyToManyValues;

          COMMIT;
        """.trimIndent()) { result ->
          if (result.failed()) callback(Exception(result.cause()).left())
          else {
            connection.query("""
              SELECT player.id, player.name from player
              inner join match_player m2 on player.id = m2.player_id
              where m2.match_id = '$newUUID'
            """.trimIndent()) { result ->
              val resultSet = result.result()
              //language=String
              val players = resultSet.rows.map { Player(it.getString("id"), it.getString("name")) }
              callback(Match(
                newUUID,
                createMatchRequest.startingDate,
                0,
                players,
                emptyList()).right()
              )

              connection.close { }
            }
          }
        }
      }
    }
  }

  fun updateMatch(match: Match): Kind<F, Match> = async { callback ->
    dbClient.getConnection { result ->
      if (result.failed()) callback(Exception(result.cause()).left())
      else {
        val connection = result.result()
        //language=PostgreSQL
        connection.execute("""
          UPDATE match SET ending_date = ${match.endingDate} WHERE match.id = '${match.id}'
        """.trimIndent()) { result ->
          if (result.failed()) callback(Exception(result.cause()).left())
          else {
            callback(match.right())
          }

          connection.close { }
        }
      }
    }
  }

}
