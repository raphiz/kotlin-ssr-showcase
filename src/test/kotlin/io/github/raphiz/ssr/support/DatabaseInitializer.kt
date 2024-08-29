package io.github.raphiz.ssr.support

import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class DatabaseInitializer(
    private val templateFile: Path,
    val username: String,
    val password: String,
) {
    private val dbFile = Files.createTempFile("testdb", ".db")

    val url = "jdbc:sqlite:${dbFile.toAbsolutePath()}"

    fun initialize() {
        Files.copy(templateFile, dbFile, StandardCopyOption.REPLACE_EXISTING)
    }

    fun close() {
        Files.delete(dbFile)
    }
}

private val databaseInitializer =
    DatabaseInitializer(
        templateFile = Path.of(System.getenv("DB_TEMPLATE_FILE")),
        username = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD"),
    ).also {
        it.initialize()
        Runtime.getRuntime().addShutdownHook(Thread { it.close() })
    }

private val dsl = DSL.using(databaseInitializer.url, databaseInitializer.username, databaseInitializer.password)

fun withDatabase(block: (dsl: DSLContext) -> Unit) {
    try {
        dsl.transaction { configuration ->
            block(configuration.dsl())
            throw RollbackException()
        }
    } catch (_: RollbackException) {
        // manual rollback - ignore
    }
}

private class RollbackException : RuntimeException()
