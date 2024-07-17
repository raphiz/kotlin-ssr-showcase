package io.github.raphiz.ssr.web

import io.github.raphiz.ssr.support.route
import io.github.raphiz.ssr.web.components.page
import kotlinx.html.*
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static

fun webAppStack(): RoutingHttpHandler {
    val webAppStack =
        routes(
            "/assets" bind static(Classpath("/assets")),
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
            Response(Status.OK).page {
                div {
                    attributes["data-controller"] = "password-visibility"
                    attributes["data-password-visibility-class"] = "hidden"
                    input {
                        type = InputType.password
                        attributes["data-password-visibility-target"] = "input"
                        attributes["spellcheck"] = "false"
                    }
                    button {
                        type = ButtonType.button
                        attributes["data-action"] = "password-visibility#toggle"
                        span {
                            attributes["data-password-visibility-target"] = "icon"
                            +"""Eye"""
                        }
                        span("hidden") {
                            attributes["data-password-visibility-target"] = "icon"
                            +"""Eye Slash"""
                        }
                    }
                }
            }
        },
    )
