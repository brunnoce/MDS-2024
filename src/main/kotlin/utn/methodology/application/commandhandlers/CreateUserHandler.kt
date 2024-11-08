package utn.methodology.application.commandhandlers

import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.User
import java.util.UUID

class CreateUserHandler(
    private val userRepository: UserRepository
) {
    fun handle(command: CreateUserCommand) {
        require(command.name.isNotBlank()) { "Name is required" }
        require(command.username.isNotBlank()) { "Username is required" }
        require(command.email.isNotBlank()) { "Email is required" }
        require(command.password.isNotBlank()) { "Password is required" }

        val user = User(
            id = UUID.randomUUID().toString(),
            name = command.name,
            username = command.username,
            email = command.email,
            password = command.password,
            followers = command.followers.toMutableList(),  
            following = command.following.toMutableList()
        )
        userRepository.save(user)
    }
}
