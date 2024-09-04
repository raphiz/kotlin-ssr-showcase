package io.github.raphiz.ssr.books.persistence

import io.github.raphiz.ssr.books.domain.Book
import io.github.raphiz.ssr.books.domain.BookRepository
import io.github.raphiz.ssr.db.tables.findBookRecordById
import io.github.raphiz.ssr.db.tables.records.BookRecord
import io.github.raphiz.ssr.db.tables.saveBookRecords
import org.jooq.DSLContext

class JooqBookRepository(
    private val dsl: DSLContext,
) : BookRepository {
    override fun save(book: Book) {
        val record = book.toRecord()
        dsl.saveBookRecords(record)
    }

    override fun findById(id: Int): Book? = dsl.findBookRecordById(id)?.toBook()
}

private fun BookRecord.toBook() = Book(id!!, title!!)

private fun Book.toRecord() = BookRecord(id, title)
