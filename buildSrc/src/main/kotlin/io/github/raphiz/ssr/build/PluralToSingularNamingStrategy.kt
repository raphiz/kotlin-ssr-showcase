package io.github.raphiz.ssr.build

import io.github.encryptorcode.pluralize.Pluralize.singular
import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.Definition

class PluralToSingularNamingStrategy : DefaultGeneratorStrategy() {
    override fun getJavaClassName(
        definition: Definition,
        mode: GeneratorStrategy.Mode,
    ): String {
        val javaClassName = super.getJavaClassName(definition, mode)
        if (javaClassName.endsWith("Record")) {
            return "${singular(javaClassName.removeSuffix("Record"))}Record"
        }
        return javaClassName
    }
}
