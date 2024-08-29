package io.github.raphiz.ssr.build

import org.flywaydb.core.Flyway
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

@CacheableTask
abstract class SqliteMigrateTask : DefaultTask() {
    @get:OutputFile
    abstract val dbFile: Property<File>

    @get:Input
    abstract val username: Property<String>

    @get:Input
    abstract val password: Property<String>

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract var migrationsLocation: Directory

    @TaskAction
    fun run() {
        val jdbcUrl = "jdbc:sqlite://${dbFile.get().absolutePath}"
        Flyway
            .configure()
            .dataSource(jdbcUrl, username.get(), password.get())
            .locations("filesystem:${migrationsLocation.asFile}")
            .cleanDisabled(false)
            .cleanOnValidationError(true)
            .load()
            .migrate()
    }
}
