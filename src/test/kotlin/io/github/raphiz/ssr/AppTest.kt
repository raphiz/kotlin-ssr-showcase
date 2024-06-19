package io.github.raphiz.ssr

import org.http4k.client.JavaHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class AppTest {
    @Test
    fun `it starts a webapp on a given port`() {
        val port = 8001
        val httpClient = JavaHttpClient()

        val response =
            startApp(app(port)) {
                httpClient(Request(Method.GET, Uri.of("http://localhost:$port")))
            }

        expectThat(response.status).isEqualTo(Status.OK)
    }

    @Test
    fun `it loads the port configuration from the environment`() {
        val port = 8002
        val environment = mapOf("SERVER_PORT" to "$port")

        val httpClient = JavaHttpClient()

        val response =
            startApp(app(environment)) {
                httpClient(Request(Method.GET, Uri.of("http://localhost:$port")))
            }

        expectThat(response.status).isEqualTo(Status.OK)
    }
}

private fun <R> startApp(
    app: Http4kServer,
    block: () -> R,
): R =
    app.use {
        app.start()
        block()
    }
