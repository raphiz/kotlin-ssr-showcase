package com.example.tables

import com.example.tables.Task.Companion.TASK
import com.example.tables.records.TaskRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.DeleteUsingStep
import org.jooq.Query
import org.jooq.Record1
import org.jooq.Result
import org.jooq.ResultQuery
import org.jooq.SelectWhereStep

fun DSLContext.findTaskRecord(query: SelectWhereStep<TaskRecord>.() -> ResultQuery<TaskRecord>): TaskRecord? =
    selectFrom(TASK).query().fetchOne()

fun DSLContext.findTaskRecordByCondition(condition: Condition): TaskRecord? = findTaskRecord { where(condition) }

fun DSLContext.listTaskRecords(query: SelectWhereStep<TaskRecord>.() -> ResultQuery<TaskRecord>): Result<TaskRecord> =
    selectFrom(TASK).query().fetch()

fun DSLContext.listTaskRecords(): Result<TaskRecord> = listTaskRecords { this }

fun DSLContext.listTaskRecordsByCondition(condition: Condition): Result<TaskRecord> = listTaskRecords { where(condition) }

fun DSLContext.countTaskRecords(query: SelectWhereStep<Record1<Int>>.() -> ResultQuery<Record1<Int>>): Int =
    selectCount().from(TASK).query().fetchOne(0, Int::class.java) ?: 0

fun DSLContext.countTaskRecords(): Int = countTaskRecords { this }

fun DSLContext.countTaskRecordsByCondition(condition: Condition): Int = countTaskRecords { where(condition) }

fun DSLContext.insertTaskRecord(record: TaskRecord): Int = executeInsert(record)

fun DSLContext.insertTaskRecords(vararg records: TaskRecord): Int = insertTaskRecords(records.toList())

fun DSLContext.insertTaskRecords(records: Collection<TaskRecord>): Int = if (records.isNotEmpty()) batchInsert(records).execute()[0] else 0

fun DSLContext.updateTaskRecord(record: TaskRecord): Int = executeUpdate(record)

fun DSLContext.updateTaskRecords(vararg records: TaskRecord): Int = updateTaskRecords(records.toList())

fun DSLContext.updateTaskRecords(records: Collection<TaskRecord>): Int = if (records.isNotEmpty()) batchUpdate(records).execute()[0] else 0

fun DSLContext.saveTaskRecord(record: TaskRecord): Int =
    insertInto(TASK)
        .set(record)
        .onDuplicateKeyUpdate()
        .set(record)
        .execute()

fun DSLContext.saveTaskRecords(vararg records: TaskRecord): Int = saveTaskRecords(records.toList())

fun DSLContext.saveTaskRecords(records: Collection<TaskRecord>): Int = if (records.isNotEmpty()) batchMerge(records).execute()[0] else 0

fun DSLContext.deleteTaskRecords(query: DeleteUsingStep<TaskRecord>.() -> Query): Int = deleteFrom(TASK).query().execute()

fun DSLContext.deleteTaskRecordsByCondition(condition: Condition): Int = deleteTaskRecords { where(condition) }

fun DSLContext.findTaskRecordById(id: Int): TaskRecord? = findTaskRecordByCondition(TASK.ID.eq(id))

fun DSLContext.listTaskRecordsByIds(vararg ids: Int): Result<TaskRecord> = listTaskRecordsByIds(ids.toList())

fun DSLContext.listTaskRecordsByIds(ids: Collection<Int>): Result<TaskRecord> = listTaskRecordsByCondition(TASK.ID.`in`(ids))

fun DSLContext.deleteTaskRecordById(id: Int): Boolean = deleteTaskRecordsByCondition(TASK.ID.eq(id)) == 1

fun DSLContext.deleteTaskRecordsByIds(vararg ids: Int): Int = deleteTaskRecordsByIds(ids.toList())

fun DSLContext.deleteTaskRecordsByIds(ids: Collection<Int>): Int = deleteTaskRecordsByCondition(TASK.ID.`in`(ids))

fun DSLContext.listTaskRecordsByName(name: String?): Result<TaskRecord> = listTaskRecordsByCondition(TASK.NAME.eq(name))

fun DSLContext.listTaskRecordsByNames(vararg names: String?): Map<String?, Result<TaskRecord>> = listTaskRecordsByNames(names.toList())

@Suppress("UNCHECKED_CAST")
fun DSLContext.listTaskRecordsByNames(names: Collection<String?>): Map<String?, Result<TaskRecord>> =
    listTaskRecordsByCondition(TASK.NAME.`in`(names)).intoGroups(TASK.NAME) as Map<String?, Result<TaskRecord>>

fun DSLContext.deleteTaskRecordsByName(name: String?): Int = deleteTaskRecordsByCondition(TASK.NAME.eq(name))

fun DSLContext.deleteTaskRecordsByNames(vararg names: String?): Int = deleteTaskRecordsByNames(names.toList())

fun DSLContext.deleteTaskRecordsByNames(names: Collection<String?>): Int = deleteTaskRecordsByCondition(TASK.NAME.`in`(names))
