plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict")) // Strict nullability java interop
    }
}

tasks.test {
    useJUnitPlatform()
}

// Make build artefacts more reporducible
// See https://github.com/raphiz/buildGradleApplication
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
