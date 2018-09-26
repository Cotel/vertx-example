package com.cotel.vertxExample

import com.cotel.vertxExample.base.mainModule
import io.vertx.core.Vertx
import org.koin.standalone.StandAloneContext.startKoin

fun main(vararg args: String) {
  startKoin(listOf(mainModule))

  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
