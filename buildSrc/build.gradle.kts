plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.0.20")

    implementation("org.flywaydb:flyway-core:10.17.2")

    implementation("org.xerial:sqlite-jdbc:3.46.0.0")

    implementation("org.jooq.jooq-codegen-gradle:org.jooq.jooq-codegen-gradle.gradle.plugin:3.19.11")
    implementation("org.jooq:jooq:3.19.11")
    implementation("org.jooq:jooq-meta:3.19.11")
    implementation("org.jooq:jooq-codegen:3.19.11")
    implementation("io.github.encryptorcode:pluralize:1.0.0")
    implementation("net.pearx.kasechange:kasechange-jvm:1.4.1")
    implementation("net.pearx.kasechange:kasechange:1.4.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.0.0")
}

tasks.test {
    useJUnitPlatform()
}
