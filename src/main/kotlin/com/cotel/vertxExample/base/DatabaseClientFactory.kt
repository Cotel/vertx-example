package com.cotel.vertxExample.base

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient

class DatabaseClientFactory {
  companion object {

    private const val URL = "url"
    private const val DRIVER = "driver_class"
    private const val MAX_POOL_SIZE = "max_pool_size-loop"

    fun createClient(vertx: Vertx): JDBCClient = JDBCClient.createShared(vertx, JsonObject(mapOf(
      URL to "jdbc:postgresql",
      DRIVER to "org.postgresql.Driver",
      MAX_POOL_SIZE to 30
    )))
  }
}
