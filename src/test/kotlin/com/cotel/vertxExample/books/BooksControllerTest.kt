package com.cotel.vertxExample.books

import com.cotel.vertxExample.MainVerticle
import com.cotel.vertxExample.base.ServiceLocator
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.storage.BooksDTO
import io.mockk.every
import io.mockk.mockk
import io.vertx.core.Vertx
import io.vertx.core.json.Json
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
class BooksControllerTest {

  private val dto = mockk<BooksDTO>()

  @BeforeEach
  fun prepare(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding { testContext.completeNow() })
  }

  @Test
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Throws(Throwable::class)
  fun `get index should return a list of books`(vertx: Vertx, testContext: VertxTestContext) {
    ServiceLocator.bind(dto)

    every { dto.getAllBooks() } returns listOf(Book(), Book())

    vertx.createHttpClient().getNow(8080, "localhost", "/books") { response ->
      testContext.verify {
        assertEquals(200, response.statusCode())
        response.handler { body ->
          val booksList: List<Book> = Json.decodeValue(body.toString("UTF-8"), List::class.java) as List<Book>
          assertTrue(booksList.size == 2)
          testContext.completeNow()
        }
      }
    }
  }

}
