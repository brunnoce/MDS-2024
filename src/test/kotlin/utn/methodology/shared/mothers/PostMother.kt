package utn.methodology.shared.mothers

import utn.methodology.domain.entities.Post
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class PostMother {

    companion object {
        private val faker = Faker()

        // Método para crear un post aleatorio
        fun random(userId: String): Post {
            val id = UUID.randomUUID()
            val message = faker.lorem.words()
            val createdAt = LocalDateTime.now().toString()
            return Post(
                id = id,
                userId = userId,
                message = message,
                createdAt = createdAt
            )
        }

        // Método para crear un post con contenido específico
        fun withContent(userId: String, content: String): Post {
            val id = UUID.randomUUID()
            val createdAt = LocalDateTime.now().toString()

            return Post(
                id = id,
                userId = userId,
                message = content,
                createdAt = createdAt
            )
        }
    }
}
