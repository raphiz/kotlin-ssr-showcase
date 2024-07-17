package io.github.raphiz.ssr.web

import io.github.raphiz.ssr.support.to
import io.github.raphiz.ssr.web.components.page
import kotlinx.html.*
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

// TODO;
// Required types:
// `Controller` marker interface
// `Target` type
// `Icon` type
// `CssClass` type

private fun index() =
    Routes.root to {
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
    }
