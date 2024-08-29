package io.github.raphiz.ssr.books.persistence

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNull

class JooqBookRepositoryTest {
    @Test
    fun `returns null for non existing id`() {
        val repository = JooqBookRepository()

        val actualBook = repository.findById(42)

        expectThat(actualBook).isNull()
    }
}
