rootProject.name = "kotlin-ssr-showcase" 

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}


plugins {
    id("com.gradle.develocity") version "3.17"
}

develocity {
     buildScan {
        termsOfUseUrl= "https://gradle.com/terms-of-service"
        termsOfUseAgree= "yes"
    }
}