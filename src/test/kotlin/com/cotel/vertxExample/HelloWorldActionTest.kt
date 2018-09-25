package com.cotel.vertxExample

import io.vertx.core.Vertx
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class HelloWorldActionTest {

  @BeforeEach
  fun prepare(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding { testContext.completeNow() })
  }

  @Test
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @DisplayName("Should show hello world phrase when requesting GET /hello")
  @Throws(Throwable::class)
  fun `get index should return hello world`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.createHttpClient().getNow(8080, "localhost", "/hello") { response ->
      testContext.verify {
        assertEquals(200, response.statusCode())
        response.handler { body ->
          assertTrue(body.toString().contains("Hello world from Vertx!"))
          testContext.completeNow()
        }
      }
    }
  }

}
