package com.cotel.vertxExample

import com.cotel.vertxExample.base.booksModule
import com.cotel.vertxExample.base.mainModule
import io.vertx.core.Vertx
import org.koin.standalone.StandAloneContext.startKoin

fun main(vararg args: String) {
  val vertx = Vertx.vertx()

  startKoin(listOf(booksModule, mainModule(vertx)))

  vertx.deployVerticle(MainVerticle())
}
