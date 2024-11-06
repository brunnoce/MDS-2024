package utn.methodology.application.commandhandlers

import utn.methodology.domain.entities.Post
import utn.methodology.infrastructure.persistence.Repositories.MongoPostRepository

class CreatePostHandler(
    private val postRepository: MongoPostRepository
) {

    fun createPost(userId: String, message: String): Post {
        if (message.length > 500) {
            throw PostValidationException("El contenido del post no puede exceder los 500 caracteres")
        }

        val post = Post(userId = userId, message = message)
        postRepository.save(post)

        return post
    }

    fun deletePost(postId: String) {
        postRepository.deleteById(postId)
    }
}

class PostValidationException(message: String) : Exception(message)
