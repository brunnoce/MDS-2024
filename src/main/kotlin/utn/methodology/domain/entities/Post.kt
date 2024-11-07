package utn.methodology.domain.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import org.bson.Document

@Serializable
data class Post(
    @Serializable(with = UUIDSerializer::class) val id: UUID,
    val userId: String,
    val message: String,
    val createdAt: String
) {
    init {
        require(message.length <= 500) { "El contenido del post no puede exceder los 500 caracteres" }
    }

    companion object {
        fun fromPrimitives(primitives: Map<String, String?>): Post {
            val id = UUID.fromString(primitives["id"] ?: throw IllegalArgumentException("El ID no puede ser nulo/null"))
            val authorId = primitives["userId"] ?: throw IllegalArgumentException("El ID del autor no puede ser nulo/null")
            val content = primitives["message"] ?: throw IllegalArgumentException("El contenido no puede ser nulo/null")
            val createdAt = primitives["createdAt"] ?: throw IllegalArgumentException("La fecha de creación no puede ser nula/null")

            return Post(id, authorId, content, createdAt)
        }

        fun fromDocument(doc: Document): Post {
            val id = UUID.fromString(doc["_id"]?.toString() ?: throw IllegalArgumentException("El ID no se encuentra"))
            val userId = doc["userId"]?.toString() ?: throw IllegalArgumentException("El userId no se encuentra")
            val message = doc["message"]?.toString() ?: throw IllegalArgumentException("El mensaje no se encuentra")
            val createdAt = doc["createdAt"]?.toString() ?: throw IllegalArgumentException("La fecha de creación no se encuentra")

            return Post(id, userId, message, createdAt)
        }
    }

    fun toPrimitives(): Map<String, String> {
        return mapOf(
            "id" to this.id.toString(),
            "userId" to this.userId,
            "message" to this.message,
            "createdAt" to this.createdAt
        )
    }
}



