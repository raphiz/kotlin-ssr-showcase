package io.github.raphiz.ssr

import io.github.raphiz.ssr.web.webAppStack
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer

private const val DEFAULT_PORT = 8000

fun app(environment: Map<String, String>): Http4kServer {
    val port = (environment["SERVER_PORT"])?.toIntOrNull() ?: DEFAULT_PORT
    return app(port)
}

fun app(port: Int): Http4kServer = webAppStack().asServer(SunHttp(port))

fun main() {
    app(System.getenv()).start().block()
}
