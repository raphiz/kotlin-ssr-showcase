package io.github.raphiz.ssr.books.domain

interface BookRepository {
    fun findById(id: Int): Book?

    fun save(book: Book)
}
