package com.cotel.vertxExample.match.storage

import arrow.Kind
import arrow.core.left
import arrow.core.right
import arrow.effects.typeclasses.Async
import com.cotel.vertxExample.match.model.CreateRoundRequest
import io.vertx.ext.jdbc.JDBCClient
import java.util.UUID

class RoundDAO<F>(
  private val dbClient: JDBCClient,
  A: Async<F>
) : Async<F> by A {

  fun createRound(matchId: String, createRoundRequest: CreateRoundRequest): Kind<F, String> = async { callback ->
    dbClient.getConnection { result ->
      if (result.failed()) callback(Exception(result.cause()).left())
      else {
        val connection = result.result()

        val newUUID = UUID.randomUUID().toString()
        with(createRoundRequest) {
          //language=PostgreSQL
          connection.execute("""
          INSERT INTO round (id, match_id, left_player_id, right_player_id, left_score, right_score, is_draw, is_left_bollo, is_right_bollo)
          VALUES ('$newUUID', '$matchId', '$leftPlayerId', '$rightPlayerId', $leftScore, $rightScore, $draw, $leftBollo, $rightBollo)
        """.trimIndent()) { result ->
            if (result.failed()) callback(Exception(result.cause()).left())
            else callback(newUUID.right())

            connection.close { }
          }
        }
      }
    }
  }

}
