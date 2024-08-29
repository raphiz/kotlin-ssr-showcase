import io.github.raphiz.ssr.build.SqliteMigrateTask

val dbTemplateFile = layout.buildDirectory.file("template.db")
val dbUsername = "sa"
val dbPassword = ""
val dbMigrationsLocation = layout.projectDirectory.dir("src/main/resources/db/migration")

// Use custom db migration task because the official
// flyway gradle plugin is badly maintained and not compatible
// with the Gradle Configuration Cache
// see https://github.com/flyway/flyway/issues/3550
val sqliteMigrate by tasks.registering(SqliteMigrateTask::class) {
    dbFile.convention(dbTemplateFile.map { it.asFile })
    username.convention(dbUsername)
    password.convention(dbPassword)
    migrationsLocation = dbMigrationsLocation
}
