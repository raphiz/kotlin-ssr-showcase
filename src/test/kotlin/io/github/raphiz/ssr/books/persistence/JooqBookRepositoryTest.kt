package io.github.raphiz.ssr.books.persistence

import io.github.raphiz.ssr.books.domain.Book
import io.github.raphiz.ssr.support.withDatabase
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class JooqBookRepositoryTest {
    @Test
    fun `returns null for non existing id`() =
        withDatabase { dsl ->
            val repository = JooqBookRepository(dsl)

            val actualBook = repository.findById(42)

            expectThat(actualBook).isNull()
        }

    @Test
    fun `returns a book by id after being inserted`() =
        withDatabase { dsl ->
            val repository = JooqBookRepository(dsl)
            val originalBook = Book(42, "Lord of the Rings")

            repository.save(originalBook)
            val actualBook = repository.findById(42)

            expectThat(actualBook).isEqualTo(originalBook)
        }
}
