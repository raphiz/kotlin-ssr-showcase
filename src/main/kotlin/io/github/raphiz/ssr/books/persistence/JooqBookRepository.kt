package io.github.raphiz.ssr.books.persistence

import io.github.raphiz.ssr.books.domain.Book
import io.github.raphiz.ssr.books.domain.BookRepository
import io.github.raphiz.ssr.db.tables.records.BookRecord
import io.github.raphiz.ssr.db.tables.references.BOOKS
import org.jooq.DSLContext

class JooqBookRepository(
    private val dsl: DSLContext,
) : BookRepository {
    override fun save(book: Book) {
        val record = book.toRecord()
        dsl.batchMerge(record).execute()
    }

    override fun findById(id: Int): Book? = dsl.selectFrom(BOOKS).where(BOOKS.ID.eq(id)).fetchOne { it.toBook() }
}

private fun BookRecord.toBook() = Book(id!!, title!!)

private fun Book.toRecord() = BookRecord(id, title)
