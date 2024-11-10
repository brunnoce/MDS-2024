package utn.methodology.application.commandhandlers

import utn.methodology.domain.contracts.UserRepository
import utn.methodology.application.commands.FollowUserCommand

class FollowUserHandler(private val userRepository: UserRepository) {
    fun handle(command: FollowUserCommand) {
        val follower = userRepository.findOne(command.followerId)
        val following = userRepository.findOne(command.followingId)

        if (follower == null || following == null) {
            throw IllegalArgumentException("Uno o ambos usuarios no existen.")
        }

        follower.following.add(command.followingId)
        following.followers.add(command.followerId)

        userRepository.save(follower)
        userRepository.save(following)
    }
}
