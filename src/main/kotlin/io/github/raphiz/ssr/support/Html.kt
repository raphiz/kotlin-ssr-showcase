package io.github.raphiz.ssr.support

import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

fun html(dsl: HTML.() -> Any?): String {
    val writer = StringBuilder()
    writer.append("<!DOCTYPE html>")
    writer.appendHTML().apply { html { dsl() } }.finalize()
    return writer.toString()
}
