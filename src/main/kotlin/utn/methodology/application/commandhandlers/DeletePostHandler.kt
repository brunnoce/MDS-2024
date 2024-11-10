package utn.methodology.application.commandhandlers

import utn.methodology.application.commands.DeletePostCommand
import utn.methodology.domain.contracts.PostRepository
import io.ktor.server.plugins.*

class DeletePostHandler(
    private val postRepository: PostRepository
) {

    fun handle(command: DeletePostCommand) {
        val post = postRepository.findById(command.postId)

        if (post == null) {
            throw NotFoundException("Post con ID: ${command.postId} no encontrado")
        }

        postRepository.deleteById(command.postId)
    }
}