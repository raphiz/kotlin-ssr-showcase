package io.github.raphiz.ssr.web

import io.github.raphiz.ssr.support.route
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes

fun webAppStack(): RoutingHttpHandler {
    val webAppStack =
        routes(
            index(),
        )

    return webAppStack
}

private object Routes {
    const val ROOT = "/"
}

private fun index() =
    Routes.ROOT.route(
        Method.GET to {
            Response(Status.OK).body("Hello World")
        },
    )
