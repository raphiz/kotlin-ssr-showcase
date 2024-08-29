plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.0.20")

    implementation("org.flywaydb:flyway-core:10.17.2")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
}
