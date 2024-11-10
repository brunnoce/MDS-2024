package utn.methodology.application.commandhandlers

import utn.methodology.domain.contracts.PostRepository
import utn.methodology.domain.entities.Post
import utn.methodology.application.commands.CreatePostCommand
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreatePostHandler(
    private val postRepository: PostRepository
) {

    fun handle(command: CreatePostCommand): Post {
        if (command.message.length > 500) {
            throw PostValidationException("El contenido del post no puede exceder los 500 caracteres")
        }

        val id = UUID.randomUUID()

        val post = Post(id = id, userId = command.userId, message = command.message, createdAt = LocalDateTime.now().format(
            DateTimeFormatter.ISO_DATE_TIME))

        postRepository.save(post)

        return post
    }
}

class PostValidationException(message: String) : Exception(message)