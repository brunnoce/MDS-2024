package utn.methodology.application.commandhandlers

import utn.methodology.domain.entities.Post
import utn.methodology.infrastructure.persistence.Repositories.MongoPostRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreatePostHandler(
    private val postRepository: MongoPostRepository
) {

    fun createPost(userId: String, message: String): Post {
        // Validación de longitud del mensaje
        if (message.length > 500) {
            throw PostValidationException("El contenido del post no puede exceder los 500 caracteres")
        }

        // Crear el ID para el post
        val id = UUID.randomUUID() // Aquí generamos el UUID para el post

        // Crear el post con el ID generado, el userId y el mensaje
        val post = Post(id = id, userId = userId, message = message, createdAt = LocalDateTime.now().format(
            DateTimeFormatter.ISO_DATE_TIME))

        // Guardar el post en la base de datos
        postRepository.save(post)

        return post
    }

    // Método para eliminar un post
    fun deletePost(postId: String) {
        postRepository.deleteById(postId)
    }
}

class PostValidationException(message: String) : Exception(message)
