import io.github.raphiz.ssr.build.SqliteMigrateTask
import io.github.raphiz.ssr.build.absolutePath

plugins {
    kotlin
    id("org.jooq.jooq-codegen-gradle")
}

val dbTemplateFile = layout.buildDirectory.file("template.db")
val dbUsername = "sa"
val dbPassword = ""
val dbMigrationsLocation = layout.projectDirectory.dir("src/main/resources/db/migration")
val dbJooqOutputDirectory = layout.buildDirectory.dir("generated/jooq")

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

sourceSets {
    main {
        java.srcDirs(dbJooqOutputDirectory)
    }
}

dependencies {
    implementation("org.jooq:jooq:3.19.11")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
}

tasks.compileKotlin.configure {
    dependsOn(tasks.jooqCodegen)
}

tasks.test {
    dependsOn(sqliteMigrate)
    environment("DB_TEMPLATE_FILE", dbTemplateFile.absolutePath)
    environment("DB_USER", dbUsername)
    environment("DB_PASSWORD", dbPassword)
    systemProperty("org.jooq.no-logo", "true")
    systemProperty("org.jooq.no-tips", "true")
}

tasks.jooqCodegen.configure {
    // db must be migrated before jooq can generate the code.
    // The jOOQ code mostly depends on the migration locations and
    // can be cached when the migrations have not changed
    dependsOn(sqliteMigrate)
    inputs.files(dbMigrationsLocation)
}

jooq {
    configuration {
        generator {
            name = "io.github.raphiz.ssr.build.CustomCodeGenerator"
            jdbc {
                url = "jdbc:sqlite://${dbTemplateFile.absolutePath}"
                user = dbUsername
                password = dbPassword
            }
            database {
                name = "org.jooq.meta.sqlite.SQLiteDatabase"
                excludes = "flyway_schema_history|sqlite_master|sqlite_sequence"
                isOutputSchemaToDefault = true
            }
            strategy {
                name = "io.github.raphiz.ssr.build.PluralToSingularNamingStrategy"
            }
            target {
                directory = dbJooqOutputDirectory.get().asFile.absolutePath
                packageName = "io.github.raphiz.ssr.db"
            }
        }
    }
}
