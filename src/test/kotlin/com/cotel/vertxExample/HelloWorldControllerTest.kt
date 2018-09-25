package com.cotel.vertxExample

import io.vertx.core.Vertx
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class HelloWorldControllerTest {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Test
  @Timeout(value = 20, timeUnit = TimeUnit.SECONDS)
  @Throws(Throwable::class)
  fun `should show hello world phrase when requesting GET hello`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.createHttpClient().getNow(8080, "localhost", "/") { response ->
      testContext.verify {
        assertEquals(200, response.statusCode())
        response.handler { body ->
          assertTrue(body.toString().contains("Hello world frin Vertx!"))
          testContext.completeNow()
        }
      }
    }
  }

}
