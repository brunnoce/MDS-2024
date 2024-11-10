package utn.methodology.application.commandhandlers

import utn.methodology.application.commands.CreateUserCommand
import utn.methodology.domain.contracts.UserRepository
import utn.methodology.domain.entities.User
import java.util.UUID

class CreateUserHandler(
    private val userRepository: UserRepository
) {
    fun handle(command: CreateUserCommand) {
        require(command.name.isNotBlank()) { "Se requiere el nombre" }
        require(command.username.isNotBlank()) { "Se requiere el username" }
        require(command.email.isNotBlank()) { "Se requiere el email" }
        require(command.password.isNotBlank()) { "Se requiere la contraseña" }

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