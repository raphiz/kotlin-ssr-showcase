package io.github.raphiz.ssr.support

import org.json.JSONObject

// locates assets from vite manifest, see https://vitejs.dev/guide/backend-integration
class AssetLocator private constructor(
    chunks: Map<String, Chunk>,
) {
    private val chunks: Map<String, Assets> =
        chunks
            .filter { it.value.isEntry }
            .mapValues { (_, chunk) ->
                Assets(
                    scripts = findScriptForEntryPoint(chunk),
                    css = findCSSForEntryPoint(chunks, chunk),
                    preloads = findModulePreloadsForEntryPoint(chunks, chunk),
                )
            }

    fun locate(entryPoint: String): Assets =
        checkNotNull(chunks[entryPoint]) {
            "$entryPoint is not an entrypoint or is missing in the manifest"
        }

    companion object {
        fun fromManifest(string: String): AssetLocator {
            val jsonManifest = JSONObject(string)
            val chunks =
                jsonManifest
                    .keys()
                    .asSequence()
                    .map { key ->
                        val jsonChunk = jsonManifest.getJSONObject(key)
                        key to
                            Chunk(
                                name = key,
                                isEntry = jsonChunk.boolean("isEntry"),
                                imports = jsonChunk.list("imports"),
                                file = jsonChunk.string("file"),
                                css = jsonChunk.list("css"),
                            )
                    }.toMap()
            return AssetLocator(chunks)
        }
    }
}

data class Assets(
    val scripts: List<String>,
    val css: List<String>,
    val preloads: List<String>,
)

private data class Chunk(
    val name: String,
    val isEntry: Boolean,
    val imports: List<String>,
    val file: String,
    val css: List<String>,
)

private fun findScriptForEntryPoint(entryPoint: Chunk): List<String> = listOf(entryPoint.file)

private fun findModulePreloadsForEntryPoint(
    chunks: Map<String, Chunk>,
    entryPoint: Chunk,
): List<String> {
    fun findImportForChunk(chunk: Chunk): List<String> {
        val importChunks = chunk.imports.map { requireNotNull(chunks[it]) { "Unknown import chunk $it" } }
        return importChunks.map { it.file } + importChunks.flatMap { findImportForChunk(it) }
    }
    return findImportForChunk(entryPoint)
}

private fun findCSSForEntryPoint(
    chunks: Map<String, Chunk>,
    entryPoint: Chunk,
): List<String> {
    fun findCSSForChunk(chunk: Chunk): List<String> =
        chunk.css + chunk.imports.flatMap { findCSSForChunk(requireNotNull(chunks[it]) { "Unknown import chunk $it" }) }
    return findCSSForChunk(entryPoint)
}

private fun JSONObject.boolean(key: String): Boolean = has(key) && getBoolean((key))

private fun JSONObject.list(key: String): List<String> =
    if (has(key)) {
        getJSONArray(key).map { it as String }
    } else {
        emptyList()
    }

private fun JSONObject.string(key: String): String = getString(key)
