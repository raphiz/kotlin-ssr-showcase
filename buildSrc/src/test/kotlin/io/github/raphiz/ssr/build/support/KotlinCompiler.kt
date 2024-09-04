package support

import org.jetbrains.kotlin.cli.common.CLITool
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import strikt.api.Assertion.Builder
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

fun Builder<Path>.compiles() {
    assert("compiles") {
        when (val exitCode = compileKotlinCode(it)) {
            ExitCode.OK -> pass()
            else -> fail("Compilation failed with exit code $exitCode")
        }
    }
}

private fun compileKotlinCode(sourceDirectory: Path): ExitCode {
    // Specify the output directory
    val outputDir = Files.createTempDirectory("compiler")

    // Use the following jars from the tests classpath for compilation
    val classPathLibraries =
        listOf(
            "jooq",
            "reactive-streams",
            "sqlite-jdbc",
            "kotlin-stdlib",
        ).map { Regex("^$it-\\d(\\.\\d+)+.jar$") }
    val classpath =
        System
            .getProperty("java.class.path")
            .split(File.pathSeparator)
            .map { Path(it).toAbsolutePath() }
            .filter { jarFile -> classPathLibraries.any { it.matches(jarFile.name) } }
            .joinToString(File.pathSeparator)

    // Compiler arguments
    val args =
        arrayOf(
            "-d",
            outputDir.pathString,
            "-classpath",
            classpath,
            "-no-stdlib",
            "-no-reflect",
            sourceDirectory.pathString,
        )
    // Compile the Kotlin files
    return CLITool.doMainNoExit(K2JVMCompiler(), args)
}
