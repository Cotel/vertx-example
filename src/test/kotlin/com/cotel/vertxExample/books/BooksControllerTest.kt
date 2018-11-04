package com.cotel.vertxExample.books

import com.cotel.vertxExample.MainVerticle
import com.cotel.vertxExample.base.booksModule
import com.cotel.vertxExample.base.mainModule
import com.cotel.vertxExample.books.model.Book
import com.cotel.vertxExample.books.storage.BooksDAO
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
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.declare
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class BooksControllerTest : KoinTest {

  private val dao = mockk<BooksDAO>()

  @BeforeEach
  fun prepare(vertx: Vertx, testContext: VertxTestContext) {
    startKoin(listOf(booksModule))
    vertx.deployVerticle(MainVerticle(), testContext.succeeding { testContext.completeNow() })
  }

  @Test
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Throws(Throwable::class)
  fun `get index should return a list of books`(vertx: Vertx, testContext: VertxTestContext) {
    declare { single(override = true) { dao } }

    every { dao.getAllBooks() } returns listOf(
      Book(101, "Test", "Test"),
      Book(1, "Test", "Test")
    )

    vertx.createHttpClient().getNow(8080, "localhost", "/books") { response ->
      testContext.verify {
        assertEquals(200, response.statusCode())
        response.handler { body ->
          val booksList = Json.decodeValue(body.toString("UTF-8"), Array<Book>::class.java)
          assertTrue(booksList.size == 2)
          assertEquals(101, booksList.first().id)
          testContext.completeNow()
        }
      }
    }
  }

}
