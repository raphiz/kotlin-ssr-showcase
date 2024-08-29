package io.github.raphiz.ssr.build

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

val Provider<RegularFile>.absolutePath: String get() = this.get().asFile.absolutePath
