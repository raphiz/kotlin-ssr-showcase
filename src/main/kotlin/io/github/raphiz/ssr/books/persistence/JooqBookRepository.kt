package io.github.raphiz.ssr.books.persistence

import io.github.raphiz.ssr.books.domain.Book
import io.github.raphiz.ssr.books.domain.BookRepository

class JooqBookRepository : BookRepository {
    override fun findById(id: Int): Book? = null
}
