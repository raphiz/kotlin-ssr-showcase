package io.github.raphiz.ssr.build
import io.github.raphiz.ssr.build.support.generateJooqModel
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo
import support.compiles
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.relativeTo

class GeneratorTest {
    private val testResources = Path.of("src/test/resources/")

    @Test
    fun `generates valid extension files`() {
        val schemaFile = testResources.resolve("schema.sql")

        val outputDirectory = generateJooqModel(schemaFile)

        expectThat(outputDirectory.listFiles()).containsExactlyInAnyOrder(
            "com/example/DefaultCatalog.kt",
            "com/example/DefaultSchema.kt",
            "com/example/keys/Keys.kt",
            "com/example/indexes/Indexes.kt",
            "com/example/tables/Task.kt",
            "com/example/tables/TaskExtensions.kt",
            "com/example/tables/records/TaskRecord.kt",
            "com/example/tables/references/Tables.kt",
        )

        expectThat(outputDirectory.resolve("com/example/tables/TaskExtensions.kt").readText())
            .isEqualTo(testResources.resolve("ExpectedTaskExtensions.kt").readText())

        expectThat(outputDirectory).compiles()
    }
}

private fun Path.listFiles(): Set<String> =
    Files
        .walk(this)
        .filter { it.isRegularFile() }
        .map { it.relativeTo(this) }
        .map { it.toString() }
        .collect(Collectors.toSet())
