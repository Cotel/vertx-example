package com.cotel.vertxExample.base

import io.vertx.core.json.JsonArray

fun <T> json(fn: () -> List<T>) = JsonArray(fn())
