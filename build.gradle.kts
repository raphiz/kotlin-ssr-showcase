plugins {
    kotlin("jvm") version "2.0.0"
    application
}

application {
    mainClass.set("io.github.raphiz.ssr.AppKt")
}

group = "io.github.raphiz.ssr"
version = System.getenv("APP_VERSION") ?: "dirty"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform("org.http4k:http4k-bom:5.23.0.0"))
    implementation("org.http4k:http4k-core")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.strikt:strikt-core:0.34.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

tasks.named<JavaExec>("run").configure {
    jvmArgs("-XX:TieredStopAtLevel=1")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
