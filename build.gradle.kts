plugins {
    kotlin
    database
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

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")

    implementation("org.json:json:20240303")
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.strikt:strikt-core:0.34.1")
}

sourceSets {
    main {
        // Add vite artifacts to be served as static assets.
        // Assets are added only for production builds via nix build.
        // The development setup uses the vite development server instead.
        output.dir(project.layout.buildDirectory.dir("resources/assets/"))
    }
}
