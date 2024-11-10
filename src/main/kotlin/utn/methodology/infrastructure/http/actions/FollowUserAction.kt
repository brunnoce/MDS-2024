package utn.methodology.infrastructure.http.actions

import utn.methodology.application.commandhandlers.FollowUserHandler
import utn.methodology.application.commands.FollowUserCommand

class FollowUserAction(private val handler: FollowUserHandler) {
    fun execute(followerId: String, followingId: String) {
        val command = FollowUserCommand(followerId, followingId)
        command.validate()
        handler.handle(command)
    }
}
