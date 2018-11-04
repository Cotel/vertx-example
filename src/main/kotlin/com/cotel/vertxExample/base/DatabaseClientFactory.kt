package com.cotel.vertxExample.base

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient

class DatabaseClientFactory {
  companion object {

    private const val URL = "url"
    private const val USER = "user"
    private const val PASSWORD = "password"
    private const val DRIVER = "driver_class"

    fun createClient(vertx: Vertx): JDBCClient = JDBCClient.createNonShared(vertx, JsonObject(mapOf(
      URL to "jdbc:postgresql://localhost:5432/bollapp",
      USER to "postgres",
      PASSWORD to "bollApp123",
      DRIVER to "org.postgresql.Driver"
    )))
  }
}
