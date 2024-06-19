package io.github.raphiz.ssr.web.components

import io.github.raphiz.ssr.support.html
import kotlinx.html.*
import org.http4k.core.ContentType
import org.http4k.core.Response

fun Response.page(content: FlowContent.() -> Unit) =
    header("content-type", ContentType.TEXT_HTML.toHeaderValue())
        .body(
            io.github.raphiz.ssr.web.components
                .page(content),
        )

fun page(content: FlowContent.() -> Unit) =
    html {
        lang = "en"
        head {
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            title { }
        }
        body {
            content()
        }
    }
