package io.github.raphiz.ssr.support

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.toPathSegmentEncoded
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

data class PathSpec(val method: Method, val pathTemplate: String) {
    operator fun invoke(vararg params: Pair<String, String>): String {
        val parameters = params.toMap()
        return pathTemplate.replace(URI_TEMPLATE_FORMAT) { matchResult ->
            val parameter = matchResult.groupValues[1]
            val paramValue =
                parameters[parameter]
                    ?: throw NoSuchElementException("No substitution for $parameter in $pathTemplate defined")
            if (paramValue.contains("/")) paramValue else paramValue.toPathSegmentEncoded()
        }
    }

    companion object {
        private val URI_TEMPLATE_FORMAT = "\\{([^}]+?)(?::([^}]+))?\\}".toRegex() // ignore redundant warning #100
    }
}

infix fun Method.at(path: String): PathSpec {
    return PathSpec(this, path)
}

infix fun PathSpec.to(action: HttpHandler): RoutingHttpHandler = this.pathTemplate bind this.method to action
