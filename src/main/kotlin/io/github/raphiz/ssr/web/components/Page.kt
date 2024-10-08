package io.github.raphiz.ssr.web.components

import io.github.raphiz.ssr.support.AssetLocator
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

val assetLocator: AssetLocator? =
    AssetLocator::class.java
        .getResource("/.vite/manifest.json")
        ?.readText()
        ?.let { AssetLocator.fromManifest(it) }
val mainAssets = assetLocator?.locate("src/main/typescript/main.ts")

fun page(content: FlowContent.() -> Unit) =
    html {
        lang = "en"
        head {
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
            title { }
            if (mainAssets != null) {
                mainAssets.css.forEach {
                    link(rel = "stylesheet", href = it) {}
                }
                mainAssets.scripts.forEach {
                    script(type = "module", src = it) {}
                }
                mainAssets.preloads.forEach {
                    script(type = "modulepreload", src = it) {}
                }
            } else {
                script(type = "module") {
                    unsafe {
                        +Resources.viteDevModeScript
                    }
                }
            }
        }
        body {
            content()
        }
    }

private object Resources {
    val viteDevModeScript =
        requireNotNull(
            this::class.java.getResourceAsStream("/vite-dev-mode.js"),
        ) { "Missing vite dev mode script" }
            .bufferedReader()
            .readText()
}
