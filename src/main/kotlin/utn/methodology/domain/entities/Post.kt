package utn.methodology.domain.entities

import kotlinx.serialization.Serializable
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

    fun toPrimitives(): Map<String, String> {
        return mapOf(
            "id" to this.id.toString(),
            "userId" to this.userId,
            "message" to this.message,
            "createdAt" to this.createdAt
        )
    }
    companion object { //NO SE USA PERO PARA SEGUIR LA ESTRUCTURA DEL USER LO CREAMOS TAMBIEN
        fun fromPrimitives(primitives: Map<String, String?>): Post {
            val id = UUID.fromString(primitives["id"] ?: throw IllegalArgumentException("El ID no puede ser nulo"))
            val authorId = primitives["userId"] ?: throw IllegalArgumentException("El ID del autor no puede ser nulo")
            val content = primitives["message"] ?: throw IllegalArgumentException("El contenido no puede ser nulo")
            val createdAt = primitives["createdAt"] ?: throw IllegalArgumentException("La fecha de creación no puede ser nula")

            return Post(id, authorId, content, createdAt)
        }
    }
}