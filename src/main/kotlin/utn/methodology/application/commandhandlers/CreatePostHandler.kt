package utn.methodology.application.commandhandlers

import utn.methodology.domain.contracts.PostRepository
import utn.methodology.domain.entities.Post
import utn.methodology.application.commands.CreatePostCommand
import utn.methodology.domain.contracts.UserRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreatePostHandler(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {

    fun handle(command: CreatePostCommand): Post {
        // Validación de userId
        val userExists = userRepository.findOne(command.userId) != null
        if (!userExists) {
            throw IllegalArgumentException("El userId proporcionado no es válido")
        }

        // Validación de mensaje
        command.validate() // Llamada a la validación que ya verifica si el mensaje está vacío

        val id = UUID.randomUUID()

        val post = Post(id = id, userId = command.userId, message = command.message, createdAt = LocalDateTime.now().format(
            DateTimeFormatter.ISO_DATE_TIME))

        postRepository.save(post)

        return post
    }
}

class PostValidationException(message: String) : Exception(message)