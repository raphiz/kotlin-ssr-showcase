package io.github.raphiz.ssr.support

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun String.route(vararg list: Pair<Method, HttpHandler>): RoutingHttpHandler =
    routes(*list.map { this bind it.first to it.second }.toTypedArray())
