package io.github.raphiz.ssr.build

import io.github.encryptorcode.pluralize.Pluralize.pluralize
import io.github.encryptorcode.pluralize.Pluralize.singular
import net.pearx.kasechange.toCamelCase
import net.pearx.kasechange.toPascalCase
import org.jooq.*
import org.jooq.codegen.GeneratorStrategy
import org.jooq.codegen.JavaWriter
import org.jooq.codegen.KotlinGenerator
import org.jooq.meta.ColumnDefinition
import org.jooq.meta.TableDefinition
import org.jooq.tools.JooqLogger
import java.io.File

class CustomCodeGenerator : KotlinGenerator() {
    private val log: JooqLogger = JooqLogger.getLogger(CustomCodeGenerator::class.java)

    override fun generateRecord(table: TableDefinition) {
        super.generateRecord(table)
        generateExtensionFunctions(table)
    }

    private fun generateExtensionFunctions(table: TableDefinition) {
        val primaryKey = table.primaryKey

        if (primaryKey == null) {
            log.info("Skipping Record Repository generation for $table because no primary key is defined.")
            return
        }

        val out = newJavaWriter(getExtensionFile(table))
        log.info("Generating record repository", out.file().getName())

        writePackageAndImports(out)

        // Get the table name (eg. TASK) and add the corresponding import
        // it is a bit hacky because it's a companion object and the jooq strategy is not handleing it properly for that
        val parts = getStrategy().getFullJavaIdentifier(table).split(".")
        val tableName = parts.last()
        out.ref((parts - tableName + "Companion" + tableName).joinToString("."))

        // Get the class name of the Record class and add the corresponding import
        val recordClassName = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.RECORD)
        out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD))

        out.writeCommonMethods(recordClassName, tableName)
        out.writePrimaryKeyExtensionFunctions(recordClassName, primaryKey.keyColumns)

        table.indexes.forEach { index ->
            // composite indices are not supported (yet?)
            val column = index.indexColumns.first().column
            out.writeColumnExtensionFunctions(recordClassName, column)
        }

        closeJavaWriter(out)
    }

    private fun JavaWriter.writeColumnExtensionFunctions(
        recordClassName: String,
        column: ColumnDefinition,
    ) {
        val identifier =
            getStrategy()
                .getFullJavaIdentifier(column)
                .split(".")
                .takeLast(2)
                .joinToString(".")
        val type = getColumnType(column)

        val nameCamelCase = column.name.toCamelCase()
        val namePascalCase = column.name.toPascalCase()

        println(
            """
            fun DSLContext.list${pluralize(recordClassName)}By$namePascalCase($nameCamelCase: $type): Result<$recordClassName> =
                list${pluralize(recordClassName)}ByCondition($identifier.eq($nameCamelCase))
            
            fun DSLContext.list${pluralize(recordClassName)}By${pluralize(namePascalCase)}(vararg ${
                pluralize(
                    nameCamelCase,
                )
            }: $type): Map<$type, Result<$recordClassName>> =
                list${pluralize(recordClassName)}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}.toList())
            
            @Suppress("UNCHECKED_CAST")
            fun DSLContext.list${pluralize(
                recordClassName,
            )}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}: Collection<$type>): Map<$type, Result<$recordClassName>> =
                list${pluralize(
                recordClassName,
            )}ByCondition($identifier.`in`(${pluralize(nameCamelCase)})).intoGroups($identifier) as Map<$type, Result<$recordClassName>>

            fun DSLContext.delete${pluralize(recordClassName)}By$namePascalCase($nameCamelCase: $type): Int =
                delete${pluralize(recordClassName)}ByCondition($identifier.eq($nameCamelCase))

            fun DSLContext.delete${pluralize(recordClassName)}By${pluralize(namePascalCase)}(vararg ${
                pluralize(
                    nameCamelCase,
                )
            }: $type): Int =
                delete${pluralize(recordClassName)}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}.toList())

            fun DSLContext.delete${pluralize(
                recordClassName,
            )}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}: Collection<$type>): Int =
                delete${pluralize(recordClassName)}ByCondition($identifier.`in`(${pluralize(nameCamelCase)}))

            """.trimIndent(),
        )
    }

    private fun JavaWriter.writePrimaryKeyExtensionFunctions(
        recordClassName: String,
        keyColumns: MutableList<ColumnDefinition>,
    ) {
        if (keyColumns.size != 1) {
            // Composite keys are currently not supported
            return
        }

        val column = keyColumns.single()
        val identifier =
            getStrategy()
                .getFullJavaIdentifier(column)
                .split(".")
                .takeLast(2)
                .joinToString(".")
        val type = getColumnType(column)

        val nameCamelCase = column.name.toCamelCase()
        val namePascalCase = column.name.toPascalCase()

        println(
            """
            fun DSLContext.find${singular(recordClassName)}By$namePascalCase($nameCamelCase: $type): $recordClassName? =
                find${singular(recordClassName)}ByCondition($identifier.eq($nameCamelCase))
            
            fun DSLContext.list${pluralize(recordClassName)}By${pluralize(namePascalCase)}(vararg ${
                pluralize(
                    nameCamelCase,
                )
            }: $type): Result<$recordClassName> =
                list${pluralize(recordClassName)}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}.toList())
            
            fun DSLContext.list${pluralize(
                recordClassName,
            )}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}: Collection<$type>): Result<$recordClassName> =
                list${pluralize(recordClassName)}ByCondition($identifier.`in`(${pluralize(nameCamelCase)}))

            fun DSLContext.delete${singular(recordClassName)}By$namePascalCase($nameCamelCase: $type): Boolean =
                delete${pluralize(recordClassName)}ByCondition($identifier.eq($nameCamelCase)) == 1

            fun DSLContext.delete${pluralize(recordClassName)}By${pluralize(namePascalCase)}(vararg ${
                pluralize(
                    nameCamelCase,
                )
            }: $type): Int =
                delete${pluralize(recordClassName)}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}.toList())

            fun DSLContext.delete${pluralize(
                recordClassName,
            )}By${pluralize(namePascalCase)}(${pluralize(nameCamelCase)}: Collection<$type>): Int =
                delete${pluralize(recordClassName)}ByCondition($identifier.`in`(${pluralize(nameCamelCase)}))

            """.trimIndent(),
        )
    }

    private fun JavaWriter.getColumnType(column: ColumnDefinition): String {
        val columnTypeDef = column.getType(resolver(this))
        val columnTypeFull = getJavaType(columnTypeDef, this)
        val columnType = ref(columnTypeFull)
        return if (columnTypeDef.isNullable) "$columnType?" else columnType
    }

    private fun writePackageAndImports(out: JavaWriter) {
        out.printPackageSpecification(getStrategy().targetPackage + ".tables")
        out.printImports()

        // Common imports
        out.ref(DSLContext::class.java)
        out.ref(Condition::class.java)
        out.ref(Result::class.java)
    }

    private fun getExtensionFile(definition: TableDefinition): File {
        val tableName = getStrategy().getJavaClassName(definition, GeneratorStrategy.Mode.DEFAULT)
        val extensionsFileName = "${tableName}Extensions"
        return getStrategy().getFile("tables/$extensionsFileName.kt")
    }
}

private fun JavaWriter.writeCommonMethods(
    recordClassName: String,
    tableIdentifier: String,
) {
    ref(ResultQuery::class.java)
    ref(SelectWhereStep::class.java)

    writeCommonFindMethods(recordClassName, tableIdentifier)
    writeCommonListMethods(recordClassName, tableIdentifier)
    writeCommonCountMethods(recordClassName, tableIdentifier)
    writeCommonInsertMethods(recordClassName)
    writeCommonUpdateMethods(recordClassName)
    writeCommonSaveMethods(recordClassName, tableIdentifier)
    writeCommonDeleteMethods(recordClassName, tableIdentifier)
}

private fun JavaWriter.writeCommonFindMethods(
    recordClassName: String,
    tableIdentifier: String,
) {
    println(
        """
        fun DSLContext.find${singular(
            recordClassName,
        )}(query: SelectWhereStep<$recordClassName>.() -> ResultQuery<$recordClassName>): $recordClassName? =
            selectFrom($tableIdentifier).query().fetchOne()

        fun DSLContext.find${singular(recordClassName)}ByCondition(condition: Condition): $recordClassName? =
            find${singular(recordClassName)} { where(condition) }

        """.trimIndent(),
    )
}

private fun JavaWriter.writeCommonListMethods(
    recordClassName: String,
    tableIdentifier: String,
) {
    println(
        """
        fun DSLContext.list${pluralize(
            recordClassName,
        )}(query: SelectWhereStep<$recordClassName>.() -> ResultQuery<$recordClassName>): Result<$recordClassName> =
            selectFrom($tableIdentifier).query().fetch()

        fun DSLContext.list${pluralize(recordClassName)}(): Result<$recordClassName> =
            list${pluralize(recordClassName)} { this }

        fun DSLContext.list${pluralize(recordClassName)}ByCondition(condition: Condition): Result<$recordClassName> =
            list${pluralize(recordClassName)} { where(condition) }

        """.trimIndent(),
    )
}

private fun JavaWriter.writeCommonCountMethods(
    recordClassName: String,
    tableIdentifier: String,
) {
    ref(Record1::class.java)
    println(
        """
        fun DSLContext.count${pluralize(recordClassName)}(query: SelectWhereStep<Record1<Int>>.() -> ResultQuery<Record1<Int>>): Int =
            selectCount().from($tableIdentifier).query().fetchOne(0, Int::class.java) ?: 0

        fun DSLContext.count${pluralize(recordClassName)}(): Int =
            count${pluralize(recordClassName)} { this }

        fun DSLContext.count${pluralize(recordClassName)}ByCondition(condition: Condition): Int =
            count${pluralize(recordClassName)} { where(condition) }

        """.trimIndent(),
    )
}

private fun JavaWriter.writeCommonInsertMethods(recordClassName: String) {
    println(
        """
        fun DSLContext.insert${singular(recordClassName)}(record: $recordClassName): Int =
            executeInsert(record)

        fun DSLContext.insert${pluralize(recordClassName)}(vararg records: $recordClassName): Int =
            insert${pluralize(recordClassName)}(records.toList())

        fun DSLContext.insert${pluralize(recordClassName)}(records: Collection<$recordClassName>): Int =
            if(records.isNotEmpty()) batchInsert(records).execute()[0] else 0

        """.trimIndent(),
    )
}

private fun JavaWriter.writeCommonUpdateMethods(recordClassName: String) {
    println(
        """
        fun DSLContext.update${singular(recordClassName)}(record: $recordClassName): Int =
            executeUpdate(record)

        fun DSLContext.update${pluralize(recordClassName)}(vararg records: $recordClassName): Int =
            update${pluralize(recordClassName)}(records.toList())

        fun DSLContext.update${pluralize(recordClassName)}(records: Collection<$recordClassName>): Int =
            if(records.isNotEmpty()) batchUpdate(records).execute()[0] else 0

        """.trimIndent(),
    )
}

private fun JavaWriter.writeCommonSaveMethods(
    recordClassName: String,
    tableIdentifier: String,
) {
    println(
        """
        fun DSLContext.save${singular(recordClassName)}(record: $recordClassName): Int =
            insertInto($tableIdentifier)
                .set(record)
                .onDuplicateKeyUpdate()
                .set(record)
                .execute()

        fun DSLContext.save${pluralize(recordClassName)}(vararg records: $recordClassName): Int =
            save${pluralize(recordClassName)}(records.toList())

        fun DSLContext.save${pluralize(recordClassName)}(records: Collection<$recordClassName>): Int =
            if(records.isNotEmpty()) batchMerge(records).execute()[0] else 0

        """.trimIndent(),
    )
}

private fun JavaWriter.writeCommonDeleteMethods(
    recordClassName: String,
    tableIdentifier: String,
) {
    ref(Query::class.java)
    ref(DeleteUsingStep::class.java)
    println(
        """
        fun DSLContext.delete${pluralize(recordClassName)}(query: DeleteUsingStep<$recordClassName>.() -> Query): Int =
            deleteFrom($tableIdentifier).query().execute()

        fun DSLContext.delete${pluralize(recordClassName)}ByCondition(condition: Condition): Int =
            delete${pluralize(recordClassName)} { where(condition) }

        """.trimIndent(),
    )
}
