package io.github.raphiz.ssr.web

import io.github.raphiz.ssr.support.route
import io.github.raphiz.ssr.web.components.page
import kotlinx.html.*
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

// TODO;
// Required types:
// `Controller` marker interface
// `Target` type
// `Icon` type
// `CssClass` type

private fun index() =
    Routes.ROOT.route(
        Method.GET to {
            Response(Status.OK).page {
                div {
                    // controller = PasswordVisibility
                    attributes["data-controller"] = "password-visibility"
                    attributes["data-password-visibility-class"] = "hidden"
                    input {
                        type = InputType.password
                        // target = PasswordVisibility.targets.input
                        attributes["data-password-visibility-target"] = "input"
                        attributes["spellcheck"] = "false"
                    }
                    button {
                        type = ButtonType.button
                        // action = PasswordVisibility.actions.toggle
                        attributes["data-action"] = "password-visibility#toggle"
                        span {
                            // target = PasswordVisibility.targets.icon
                            attributes["data-password-visibility-target"] = "icon"
                            +"""Eye"""
                        }
                        span("hidden") {
                            // target = PasswordVisibility.targets.icon
                            attributes["data-password-visibility-target"] = "icon"
                            +"""Eye Slash"""
                        }
                    }
                }
            }
        },
    )
