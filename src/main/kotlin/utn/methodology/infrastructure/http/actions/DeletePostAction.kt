package utn.methodology.infrastructure.http.actions

import utn.methodology.application.commandhandlers.DeletePostHandler
import utn.methodology.application.commands.DeletePostCommand
import io.ktor.http.*

class DeletePostAction(
    private val handler: DeletePostHandler
) {

    fun execute(parameters: Parameters) {
        val postId = parameters["id"].toString()

        val command = DeletePostCommand(postId)

        command.validate().let {
            handler.handle(it)
        }
    }
}