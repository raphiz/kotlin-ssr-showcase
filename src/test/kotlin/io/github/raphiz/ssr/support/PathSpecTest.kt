package io.github.raphiz.ssr.support

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message

class PathSpecTest {
    @Test
    fun `just returns path when no path variables are present`() {
        val pathSpec = PathSpec(GET, "/foo/bar")
        val generated = pathSpec()
        expectThat(generated).isEqualTo("/foo/bar")
    }

    @Test
    fun `replaces all given path variables`() {
        val pathSpec = PathSpec(GET, "/a/{type}/b/{name}/c")
        val generated =
            pathSpec(
                "name" to "coffee",
                "type" to "beverage",
            )
        expectThat(generated).isEqualTo("/a/beverage/b/coffee/c")
    }

    @Test
    fun `throws when missing any path variable`() {
        val pathSpec = PathSpec(GET, "/a/{type}/b/{name}/c")

        expectThrows<NoSuchElementException> {
            pathSpec("type" to "beverage")
        }.message.isEqualTo("No substitution for name in /a/{type}/b/{name}/c defined")
    }

    @Test
    fun `infix function is syntactic sugar for constructor`() {
        val fromConstructorCall = PathSpec(GET, "/a/{type}/b/{name}/c")
        val fromInfixFunction = GET at "/a/{type}/b/{name}/c"
        expectThat(fromConstructorCall).isEqualTo(fromInfixFunction)
    }

    @Test
    fun `can be routed`() {
        val routes =
            GET at "/foo" to {
                Response(Status.OK)
            }

        expectThat(routes(Request(GET, "/foo"))).isEqualTo(Response(Status.OK))
        expectThat(routes(Request(GET, "/"))).isEqualTo(Response(Status.NOT_FOUND))
    }
}
