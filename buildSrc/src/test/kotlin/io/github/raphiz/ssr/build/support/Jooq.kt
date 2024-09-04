package io.github.raphiz.ssr.build.support

import org.jooq.codegen.GenerationTool
import org.jooq.impl.DSL
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

fun generateJooqModel(schemaFile: Path): Path {
    val outputDirectory = Files.createTempDirectory("test")

    val dbFile = Files.createTempFile("test", "db").also { it.toFile().deleteOnExit() }
    val dbJdbcUrl = "jdbc:sqlite:${dbFile.absolutePathString()}"
    val dbUsername = "sa"
    val dbPassword = ""

    // Execute schema creation script
    DSL.using(dbJdbcUrl, dbUsername, dbPassword).use { ctx ->
        schemaFile
            .readText()
            .split(";")
            .forEach {
                if (it.isNotBlank()) ctx.execute(it)
            }
    }

    val configuration =
        Configuration()
            .withJdbc(
                Jdbc()
                    .withUrl(dbJdbcUrl)
                    .withUser(dbUsername)
                    .withPassword(dbPassword),
            ).withGenerator(
                Generator()
                    .withName("io.github.raphiz.ssr.build.CustomCodeGenerator")
                    .withDatabase(
                        Database()
                            .withName("org.jooq.meta.sqlite.SQLiteDatabase")
                            .withIncludes(".*")
                            .withExcludes("sqlite_sequence"),
                    ).withTarget(
                        Target()
                            .withPackageName("com.example")
                            .withDirectory(outputDirectory.absolutePathString()),
                    ),
            )

    GenerationTool.generate(configuration)
    return outputDirectory
}
